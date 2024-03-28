package com.fuzzy.main.platform.component.frontend.engine.network.protocol;

import com.fuzzy.main.network.event.NetworkListener;
import com.fuzzy.main.network.session.Session;
import com.fuzzy.main.network.session.SessionImpl;
import com.fuzzy.main.platform.component.frontend.engine.network.subscriber.WebSocketSubscriber;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class GraphQLSubscriber implements NetworkListener {

    private final Map<Session, CopyOnWriteArrayList<WebSocketSubscriber>> sessions;

    public GraphQLSubscriber() {
        sessions = new ConcurrentHashMap<>();
    }

    public void registry(WebSocketSubscriber webSocketSubscriber) {
        Session session = webSocketSubscriber.transportSession.getSession();

        CopyOnWriteArrayList<WebSocketSubscriber> subscribers = sessions.get(session);
        if (subscribers == null) {
            synchronized (sessions) {
                subscribers = sessions.get(session);
                if (subscribers == null) {
                    subscribers = new CopyOnWriteArrayList<WebSocketSubscriber>();
                    sessions.put(session, subscribers);
                }
            }
        }

        subscribers.add(webSocketSubscriber);

        //Подписываемся на сетевое соединение
        webSocketSubscriber.transportSession.addListener(this);
    }

    /**
     * Отписываемся от определенной подписки
     *
     * @param session
     * @param packetId
     */
    public void unSubscriber(Session session, Serializable packetId) {
        CopyOnWriteArrayList<WebSocketSubscriber> subscribers = sessions.get(session);
        if (subscribers == null) return;

        for (WebSocketSubscriber subscriber : subscribers) {
            if (packetId.equals(subscriber.packetId)) {
                subscriber.unSubscriber();
                subscribers.remove(subscriber);
                break;
            }
        }

        //Список стал пустым - стоит почистить
        if (subscribers.isEmpty()) {
            synchronized (sessions) {
                if (subscribers.isEmpty()) {
                    sessions.remove(session);
                }
            }
        }
    }

    @Override
    public void onConnect(Session session) {
        //Игнорим событие подключение
    }

    @Override
    public void onDisconnect(Session session) {
        //При разрыве сетевого соединения - удаляем себя из подписчиков и отменяем подписку
        ((SessionImpl) session).getTransportSession().removeListener(this);

        CopyOnWriteArrayList<WebSocketSubscriber> subscribers = sessions.remove(session);
        if (subscribers != null) {
            for (WebSocketSubscriber subscriber : subscribers) {
                subscriber.unSubscriber();
            }
        }
    }
}
