package com.fuzzy.subsystem.gameserver.xml;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.extensions.scripts.Scripts;
import com.fuzzy.subsystem.gameserver.idfactory.IdFactory;
import com.fuzzy.subsystem.gameserver.model.*;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.skills.DocumentItem;
import com.fuzzy.subsystem.gameserver.skills.SkillTimeStamp;
import com.fuzzy.subsystem.gameserver.templates.L2Item;
import com.fuzzy.subsystem.gameserver.xml.loader.*;
import com.fuzzy.subsystem.gameserver.xml.loader.XmlEtcItemLoader;
import com.fuzzy.subsystem.gameserver.xml.loader.XmlWeaponLoader;
import com.fuzzy.subsystem.util.GArray;
import com.fuzzy.subsystem.util.Log;
import com.fuzzy.subsystem.util.Util;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author : Ragnarok
 * @date : 03.01.11    11:27
 */
public class ItemTemplates {
    private static ItemTemplates ourInstance;

    private Logger log = Logger.getLogger(ItemTemplates.class.getName());
    private ConcurrentHashMap<Integer, L2Item> allTemplates = new ConcurrentHashMap<Integer, L2Item>();
    public static final HashMap<Integer, List<Integer>> _reuseGroups = new HashMap<Integer, List<Integer>>();
    private List<L2Item> droppableTemplates;

    public static ItemTemplates getInstance() {
        if (ourInstance == null)
            ourInstance = new ItemTemplates();
        return ourInstance;
    }

    private ItemTemplates() {
        load();
    }

    private void load() {
        allTemplates.putAll(XmlArmorLoader.getInstance().getArmors());
        allTemplates.putAll(XmlWeaponLoader.getInstance().getWeapons());
        allTemplates.putAll(XmlEtcItemLoader.getInstance().getEtcItems());

        // TODO: Перенести все эти статы в сами xml файлы соответствующих итемов
        new Thread(new com.fuzzy.subsystem.common.RunnableImpl() {
            public void runImpl() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                new DocumentItem(new File("./data/stats/items/tattoo.xml"));
            }
        }).start();
    }

    public L2Item getTemplate(int id) {
        return getTemplate(id, true);
    }

    public L2Item getTemplate(int id, boolean toLog) {
        if (allTemplates.get(id) == null) {
            if (toLog) {
                log.warning("Not defined item_id=" + id + ", or out of range");
                Thread.dumpStack();
            }
            return null;
        }
        return allTemplates.get(id);
    }

    public L2ItemInstance createItem(int itemId) {
        if (Util.contains_int(ConfigValue.DisableCreateItems, itemId)) {
            Log.displayStackTrace(new Throwable(), "Try creating DISABLE_CREATION item " + itemId);
            return null;
        }
        return new L2ItemInstance(IdFactory.getInstance().getNextId(), itemId);
    }

    public Collection<L2Item> getAllTemplates() {
        return allTemplates.values();
    }

    public List<Integer> getItemFoGrup(Integer grp_id) {
        if (_reuseGroups.containsKey(grp_id))
            return _reuseGroups.get(grp_id);
        return null;
    }

    public HashMap<Integer, List<Integer>> getItemFoGrup() {
        return _reuseGroups;
    }

    // Если стоит реюз итема, то отправляем оставшееся время до конца реюза...
    public static long useHandler(L2Playable self, L2ItemInstance item, Boolean ctrl) {
        L2Player player;
        if (self.isPlayer())
            player = (L2Player) self;
        else if (self.isPet())
            player = self.getPlayer();
        else
            return -1;

        // Вызов всех определенных скриптовых итемхэндлеров
        GArray<Scripts.ScriptClassAndMethod> handlers = Scripts.itemHandlers.get(item.getItemId());
        if (handlers != null && handlers.size() > 0) {
			/*if(player.isInFlyingTransform())
			{
				player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
				return -1;
			}*/

            Object[] script_args = new Object[]{player, ctrl};
            for (Scripts.ScriptClassAndMethod handler : handlers)
                player.callScripts(handler.scriptClass, handler.method, script_args);
            return 0;
        }

        L2Skill[] skills = item.getItem().getAttachedSkills();
        if (item.getItem().isConsume())
            if (item == null || item.getCount() < 1)
                return 0;

        if (skills != null && skills.length > 0) {
            int item_disable_id;
            int grp_id = item.getItem().delay_share_group;

            if (grp_id > 0)
                item_disable_id = (grp_id * 65536 + item.getItemId()) * -1;
            else {
                item_disable_id = item.getItemId() * -1;
                grp_id = item.getItemId();
            }

            if (!player.isSkillDisabled(ConfigValue.SkillReuseType == 0 ? item_disable_id * 65536L + 1 : item_disable_id)) {
                boolean _consume = false;
                for (L2Skill skill : skills) {
                    L2Character aimingTarget = skill.getAimingTarget(player, player.getTarget());

                    /**
                     * immediate_effect=0 - задается откат итему и скилу.
                     * immediate_effect=1 - задается откат только итему.
                     **/
                    if (item.getItem().immediate_effect == 1) {
                        int condResult = skill.condition(player, aimingTarget, true);
                        if (condResult == -1)
                            return -1;
                        if (item.getItem().isConsume() && !_consume) {
                            if (self.getInventory().destroyItem(item, 1, false) == null)
                                return 0;
                            _consume = true;
                        }
                        player.altUseSkill(skill, aimingTarget, false);
                        player.disableItem(item_disable_id, 1/*skill.getLevel()*/, item.getItemId(), grp_id, item.getReuseDelay(), item.getReuseDelay());
                    } else {
                        // Итем отключаем даже если не прошли условия, тестил на ПТСке.
                        player.disableItem(item_disable_id, 1/*skill.getLevel()*/, item.getItemId(), grp_id, item.getReuseDelay(), item.getReuseDelay());
                        if (skill.checkCondition(player, aimingTarget, ctrl, false, true)) {
                            if (item.getItem().isConsume() && !_consume) {
                                if (self.getInventory().destroyItem(item, 1, false) == null)
                                    return 0;
                                _consume = true;
                            }
                            player.getAI().Cast(skill, aimingTarget, ctrl, false);
                        }
                    }
                }
                return 0;
            } else {
                SkillTimeStamp sts = player.getSkillReuseTimeStamps().get(ConfigValue.SkillReuseType == 0 ? item_disable_id * 65536L + 1 : item_disable_id);
                if (sts == null)
                    return 0;
                else if (!sts.hasNotPassed())
                    return 100L;
                long timeleft = sts.getReuseCurrent();
                long hours = timeleft / 3600000;
                long minutes = (timeleft - hours * 3600000) / 60000;
                long seconds = (long) Math.ceil((timeleft - hours * 3600000 - minutes * 60000) / 1000.);
                if (hours > 0)
                    player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addItemName(item.getItemId()).addNumber(hours).addNumber(minutes).addNumber(seconds));
                else if (minutes > 0)
                    player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addItemName(item.getItemId()).addNumber(minutes).addNumber(seconds));
                else
                    player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addItemName(item.getItemId()).addNumber(seconds));
                return sts.getReuseCurrent();
            }
        }
        return -1;
    }

    public boolean consumeItem(final int itemConsumeId, final int itemCount) {
        return false;
    }

    public List<L2Item> getItemsByNameContainingString(CharSequence name, boolean onlyDroppable) {
        Collection<L2Item> toChooseFrom = onlyDroppable ? getDroppableTemplates() : getAllTemplates();
        List<L2Item> templates = new ArrayList<L2Item>();
        for (L2Item template : toChooseFrom)
            if (template != null && StringUtils.containsIgnoreCase(template.getName(), name))
                templates.add(template);
        return templates;
    }

    public List<L2Item> getDroppableTemplates() {
        if (droppableTemplates == null) {
            droppableTemplates = new ArrayList<L2Item>();
            droppableTemplates.addAll(CalculateRewardChances.getDroppableItems());
        }
        return droppableTemplates;
    }
}
