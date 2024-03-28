package com.fuzzy.main.cluster.graphql.schema.build.graphqltype;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.core.remote.utils.validatorremoteobject.RemoteObjectValidator;
import com.fuzzy.main.cluster.core.remote.utils.validatorremoteobject.ResultValidator;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObjectInterface;
import com.fuzzy.main.cluster.graphql.exception.GraphQLExecutorException;
import com.fuzzy.main.cluster.graphql.preparecustomfield.PrepareCustomField;
import com.fuzzy.main.cluster.graphql.schema.GraphQLSchemaType;
import com.fuzzy.main.cluster.graphql.schema.scalartype.GraphQLTypeScalar;
import com.fuzzy.main.cluster.graphql.schema.struct.RGraphQLType;
import com.fuzzy.main.cluster.graphql.schema.struct.RGraphQLTypeEnum;
import com.fuzzy.main.cluster.graphql.schema.struct.in.RGraphQLInputObjectTypeField;
import com.fuzzy.main.cluster.graphql.schema.struct.in.RGraphQLTypeInObject;
import com.fuzzy.main.cluster.graphql.schema.struct.out.RGraphQLObjectTypeField;
import com.fuzzy.main.cluster.graphql.schema.struct.out.RGraphQLObjectTypeMethodArgument;
import com.fuzzy.main.cluster.graphql.schema.struct.out.RGraphQLTypeOutObject;
import com.fuzzy.main.cluster.graphql.schema.struct.out.RGraphQLTypeOutObjectInterface;
import com.fuzzy.main.cluster.graphql.struct.GOptional;
import com.fuzzy.main.cluster.graphql.struct.GSubscribeEvent;
import com.fuzzy.main.cluster.struct.Component;
import com.fuzzy.main.cluster.utils.ReflectionUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * Created by kris on 30.12.16.
 */
public class TypeGraphQLBuilder {

    private final static Logger log = LoggerFactory.getLogger(TypeGraphQLBuilder.class);

    private final UUID nodeRuntimeId;
    private final Integer componentId;
    private final String packageName;

    private final GraphQLSchemaType graphQLSchemaType;

    private TypeGraphQLFieldConfigurationBuilder fieldConfigurationBuilder;

    public TypeGraphQLBuilder(Component component, GraphQLSchemaType graphQLSchemaType) {
        this.nodeRuntimeId = component.getRemotes().cluster.node.getRuntimeId();
        this.componentId = component.getId();
        this.packageName = component.getInfo().getUuid();

        this.graphQLSchemaType = graphQLSchemaType;
    }

    public TypeGraphQLBuilder(String packageName, GraphQLSchemaType graphQLSchemaType) {
        this.nodeRuntimeId = null;
        this.componentId = null;
        this.packageName = packageName;

        this.graphQLSchemaType = graphQLSchemaType;
    }

    public TypeGraphQLBuilder withFieldConfigurationBuilder(TypeGraphQLFieldConfigurationBuilder fieldConfigurationBuilder) {
        this.fieldConfigurationBuilder = fieldConfigurationBuilder;
        return this;
    }

    public Map<Class, RGraphQLType> build() throws GraphQLExecutorException {
        Reflections reflections = new Reflections(packageName, new Scanner[0]);

        Map<Class, RGraphQLType> rTypeGraphQLItems = new HashMap<Class, RGraphQLType>();
        for (Class classRTypeGraphQL : reflections.getTypesAnnotatedWith(com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject.class, true)) {
            com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject aGraphQLType = (com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject) classRTypeGraphQL.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject.class);

            //Имя типа
            String name = aGraphQLType.value();

            RGraphQLType rGraphQLType;
            if (classRTypeGraphQL.isEnum()) {
                Set<String> enumValues = null;
                enumValues = new HashSet<String>();
                for (Object oEnum : classRTypeGraphQL.getEnumConstants()) {
                    enumValues.add(((Enum) oEnum).name());
                }

                com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription aGraphQLDescription = (com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription) classRTypeGraphQL.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription.class);
                String description = (aGraphQLDescription != null && !aGraphQLDescription.value().isEmpty()) ? aGraphQLDescription.value() : null;

                rGraphQLType = new RGraphQLTypeEnum(name, description, enumValues);
            } else {
                //Собираем union типы
                Set<String> unionGraphQLTypeNames = new HashSet<String>();
                findUnionGraphQLTypeNames(classRTypeGraphQL, unionGraphQLTypeNames);

                //Собираем поля
                Set<RGraphQLObjectTypeField> fields = new HashSet<RGraphQLObjectTypeField>();

                //Обрабытываем поля
                for (Field field : classRTypeGraphQL.getDeclaredFields()) {
                    com.fuzzy.main.cluster.graphql.anotation.GraphQLField aGraphQLField = field.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLField.class);
                    if (aGraphQLField == null) continue;

                    //Проверяем, что возвращаемый тип сериализуем
                    if (!RemoteObject.class.isAssignableFrom(field.getDeclaringClass())
                            && !Serializable.class.isAssignableFrom(field.getType())) {
                        throw new GraphQLExecutorException("Field: " + field.getName() + " in class " + classRTypeGraphQL.getName() + " return type is not serializable");
                    }

                    String typeField;
                    try {
                        typeField = getGraphQLType(field.getGenericType());
                    } catch (Exception e) {
                        throw new RuntimeException("Exception build type, class: " + classRTypeGraphQL.getName() + ", field: " + field.getName(), e);
                    }

                    String nameField = field.getName();

                    String graphQLFieldName = getGraphQLFieldName(field);
                    String graphQLFieldDeprecated = getGraphQLFieldDeprecated(field);

                    RemoteObject fieldConfiguration = null;
                    if (fieldConfigurationBuilder != null) {
                        fieldConfiguration = fieldConfigurationBuilder.build(classRTypeGraphQL, field);
                    }

                    com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription aGraphQLDescription = field.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription.class);
                    String description = (aGraphQLDescription != null && !aGraphQLDescription.value().isEmpty()) ? aGraphQLDescription.value() : null;

                    fields.add(new RGraphQLObjectTypeField(nodeRuntimeId, componentId, true, false, typeField, nameField, graphQLFieldName, fieldConfiguration, description, graphQLFieldDeprecated));
                }

                //Обрабатываем методы
                for (Method method : classRTypeGraphQL.getMethods()) {
                    if (method.isSynthetic()) continue;//Игнорируем генерируемые методы

                    com.fuzzy.main.cluster.graphql.anotation.GraphQLField aGraphQLField = method.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLField.class);
                    if (aGraphQLField == null) continue;

                    fields.add(buildRGraphQLObjectTypeField(nodeRuntimeId, componentId, classRTypeGraphQL, method, aGraphQLField));
                }

                //Собираем статические методы из зависимых интерфейсов
                for (Class subInterface : classRTypeGraphQL.getInterfaces()) {
                    for (Method method : subInterface.getMethods()) {
                        if (method.isSynthetic()) continue;//Игнорируем генерируемые методы
                        if (!Modifier.isStatic(method.getModifiers())) continue;//Ищем только статические методы

                        com.fuzzy.main.cluster.graphql.anotation.GraphQLField aGraphQLField = method.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLField.class);
                        if (aGraphQLField == null) continue;

                        fields.add(buildRGraphQLObjectTypeField(nodeRuntimeId, componentId, classRTypeGraphQL, method, aGraphQLField));
                    }
                }

                com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription aTypeGraphQLDescription = (com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription) classRTypeGraphQL.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription.class);
                String description = (aTypeGraphQLDescription != null && !aTypeGraphQLDescription.value().isEmpty()) ? aTypeGraphQLDescription.value() : null;

                rGraphQLType = new RGraphQLTypeOutObject(name, description, classRTypeGraphQL.getName(), unionGraphQLTypeNames, fields);
            }

            rTypeGraphQLItems.put(classRTypeGraphQL, rGraphQLType);
        }

        for (Class classRTypeGraphQL : reflections.getTypesAnnotatedWith(GraphQLTypeOutObjectInterface.class, true)) {
            GraphQLTypeOutObjectInterface aGraphQLTypeOutObjectUnion = (GraphQLTypeOutObjectInterface) classRTypeGraphQL.getAnnotation(GraphQLTypeOutObjectInterface.class);

            String name = aGraphQLTypeOutObjectUnion.value();

            //Собираем поля
            Set<RGraphQLObjectTypeField> fields = new HashSet<RGraphQLObjectTypeField>();
            for (Method method : classRTypeGraphQL.getMethods()) {
                if (method.isSynthetic()) continue;//Игнорируем генерируемые методы

                com.fuzzy.main.cluster.graphql.anotation.GraphQLField aGraphQLField = method.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLField.class);
                if (aGraphQLField == null) continue;

                fields.add(buildRGraphQLObjectTypeField(nodeRuntimeId, componentId, classRTypeGraphQL, method, aGraphQLField));
            }

            com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription aGraphQLDescription = (com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription) classRTypeGraphQL.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription.class);
            String description = (aGraphQLDescription != null && !aGraphQLDescription.value().isEmpty()) ? aGraphQLDescription.value() : null;

            RGraphQLTypeOutObjectInterface rGraphQLType = new RGraphQLTypeOutObjectInterface(name, description, fields);
            rTypeGraphQLItems.put(classRTypeGraphQL, rGraphQLType);
        }

        for (Class classRTypeGraphQL : reflections.getTypesAnnotatedWith(com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeInput.class)) {
            com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeInput aGraphQLTypeInput = (com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeInput) classRTypeGraphQL.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeInput.class);

            String name = aGraphQLTypeInput.value();
            Set<RGraphQLInputObjectTypeField> fields = new HashSet<RGraphQLInputObjectTypeField>();

            //Обрабытываем поля
            Constructor constructor = com.fuzzy.main.cluster.graphql.utils.ReflectionUtils.getGConstructor(classRTypeGraphQL);
            if (constructor == null) {
                throw new RuntimeException("Not found constructor from GraphQLTypeInput: " + classRTypeGraphQL.getName());
            }
            Annotation[][] annotations = constructor.getParameterAnnotations();
            AnnotatedType[] annotatedType = constructor.getAnnotatedParameterTypes();
            Type[] fieldTypes = constructor.getGenericParameterTypes();
            for (int index = 0; index < fieldTypes.length; index++) {
                String typeField;
                try {
                    typeField = getGraphQLType(fieldTypes[index]);
                } catch (Exception e) {
                    throw new RuntimeException("Exception build type, constructor: " + constructor + ", argument index: " + index, e);
                }

                String nameField = null;
                for (Annotation iAnnotation : annotations[index]) {
                    if (iAnnotation.annotationType() == com.fuzzy.main.cluster.graphql.anotation.GraphQLName.class) {
                        nameField = ((com.fuzzy.main.cluster.graphql.anotation.GraphQLName) iAnnotation).value();
                    }
                }
                if (nameField == null) {
                    throw new RuntimeException("Exception build type, constructor: " + constructor + ". Not fount annotation GraphQLName, argument index: " + index);
                }

                boolean isNotNull = false;
                for (Annotation iAnnotation : annotatedType[index].getAnnotations()) {
                    if (iAnnotation.annotationType() == NonNull.class) {
                        isNotNull = true;
                    }
                }

                fields.add(new RGraphQLInputObjectTypeField(typeField, nameField, nameField, isNotNull));
            }

            com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription aGraphQLDescription = (com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription) classRTypeGraphQL.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription.class);
            String description = (aGraphQLDescription != null && !aGraphQLDescription.value().isEmpty()) ? aGraphQLDescription.value() : null;

            RGraphQLTypeInObject rGraphQLType = new RGraphQLTypeInObject(name, description, fields);
            rTypeGraphQLItems.put(classRTypeGraphQL, rGraphQLType);
        }

        return rTypeGraphQLItems;
    }

    private RGraphQLObjectTypeField buildRGraphQLObjectTypeField(UUID nodeRuntimeId, Integer componentId, Class classRTypeGraphQL, Method method, com.fuzzy.main.cluster.graphql.anotation.GraphQLField aGraphQLField) throws GraphQLExecutorException {
        //Если родительский класс не реализовывает интерфейс RemoteObject, то все его поля могут быть только статическими
        if (!RemoteObject.class.isAssignableFrom(method.getDeclaringClass())) {
            if (!Modifier.isStatic(method.getModifiers()))
                throw new GraphQLExecutorException("Method " + method.getName() + " in class " + method.getDeclaringClass().getName() + " is not static");
        }

        PrepareCustomField prepareCustomField = checkPrepareField(graphQLSchemaType, method.getReturnType());
        boolean isPrepereField = (prepareCustomField != null);

        //Prepere поля должны быть обязательно static
        if (isPrepereField) {
            if (!Modifier.isStatic(method.getModifiers()))
                throw new GraphQLExecutorException("Method " + method.getName() + " in class " + method.getDeclaringClass().getName() + " is not static");
        }

        //Проверяем, что возвращаемый тип сериализуем
        if (isPrepereField) {
            Type endType = prepareCustomField.getEndType(method.getGenericReturnType());
//			RemoteObjectValidator.validation(endType);
            Class endClazz = ReflectionUtils.getRawClass(endType);
            if (!(endClazz instanceof Serializable)) {
                throw new GraphQLExecutorException("Method: " + method.getName() + " in class " + method.getDeclaringClass().getName() + " return type is not serializable");
            }
        } else if (method.getReturnType().isAssignableFrom(GSubscribeEvent.class)) {
            //Подписка
            Type endType = method.getGenericReturnType();
            //RemoteObjectValidator.validation(endType);
            Class endClazz = ReflectionUtils.getRawClass(endType);
            if (!(endClazz instanceof Serializable)) {
                throw new GraphQLExecutorException("Method: " + method.getName() + " in class " + method.getDeclaringClass().getName() + " return type is not serializable");
            }
        } else {
            ResultValidator resultValidator = RemoteObjectValidator.validation(method.getGenericReturnType());
            if (!resultValidator.isSuccess()) {
                throw new GraphQLExecutorException("Method: " + method.getName() + " in class " + method.getDeclaringClass().getName() + " return type is not serializable. ResultValidator: " + resultValidator.toString());
            }
        }

        String typeField;
        try {
            typeField = getGraphQLType(method.getGenericReturnType());
        } catch (Exception e) {
            throw new GraphQLExecutorException("Exception build type, class: " + classRTypeGraphQL.getName() + ", method: " + method.getName(), e);
        }

        String nameMethod = method.getName();

        String graphQLFieldName = getGraphQLFieldName(method);
        String graphQLFieldDeprecated = getGraphQLFieldDeprecated(method);

        List<RGraphQLObjectTypeMethodArgument> arguments = new ArrayList<>();
        Class[] parameterTypes = method.getParameterTypes();
        Annotation[][] parametersAnnotations = method.getParameterAnnotations();
        AnnotatedType[] annotatedParameterTypes = method.getAnnotatedParameterTypes();
        for (int index = 0; index < parameterTypes.length; index++) {
            //Собираем аннотации
            com.fuzzy.main.cluster.graphql.anotation.GraphQLSource aGraphQLTarget = null;
            com.fuzzy.main.cluster.graphql.anotation.GraphQLName aGraphQLName = null;
            com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription aGraphQLDescription = null;
            for (Annotation annotation : parametersAnnotations[index]) {
                if (annotation.annotationType() == com.fuzzy.main.cluster.graphql.anotation.GraphQLSource.class) {
                    if (!RemoteObject.class.isAssignableFrom(parameterTypes[index])) {
                        throw new RuntimeException("Class does not implement interface RemoteObject: " + parameterTypes[index]);
                    }
                    aGraphQLTarget = (com.fuzzy.main.cluster.graphql.anotation.GraphQLSource) annotation;
                } else if (annotation.annotationType() == com.fuzzy.main.cluster.graphql.anotation.GraphQLName.class) {
                    aGraphQLName = (com.fuzzy.main.cluster.graphql.anotation.GraphQLName) annotation;
                } else if (annotation.annotationType() == com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription.class) {
                    aGraphQLDescription = (com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription) annotation;
                }
            }
            boolean isNotNull = false;
            for (Annotation annotation : annotatedParameterTypes[index].getAnnotations()) {
                if (annotation.annotationType() == NonNull.class) {
                    isNotNull = true;
                }
            }

            if (aGraphQLTarget != null) continue;//В эту переменную будет передаваться объект для которого вызывается
            if (aGraphQLName == null) continue;//В эту переменную будет передаваться внешняя переменная

            String typeArgument;
            try {
                typeArgument = getGraphQLType(method.getGenericParameterTypes()[index]);
            } catch (Exception e) {
                throw new RuntimeException("Exception build type, class: " + classRTypeGraphQL.getName() + ", method: " + method.getName(), e);
            }
            String nameArgument = aGraphQLName.value();

            String descriptionArgument = (aGraphQLDescription != null && !aGraphQLDescription.value().isEmpty()) ? aGraphQLDescription.value() : null;

            arguments.add(new RGraphQLObjectTypeMethodArgument(typeArgument, nameArgument, nameArgument, isNotNull, descriptionArgument));
        }

        RemoteObject fieldConfiguration = null;
        if (fieldConfigurationBuilder != null) {
            fieldConfiguration = fieldConfigurationBuilder.build(classRTypeGraphQL, method);
        }

        com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription aGraphQLDescription = method.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription.class);
        String description = (aGraphQLDescription != null && !aGraphQLDescription.value().isEmpty()) ? aGraphQLDescription.value() : null;

        return new RGraphQLObjectTypeField(nodeRuntimeId, componentId, false, isPrepereField, typeField, nameMethod, graphQLFieldName, arguments, fieldConfiguration, description, graphQLFieldDeprecated);
    }

    private String getGraphQLType(Type type) throws ClassNotFoundException {
        Class rawType;
        if (type instanceof ParameterizedType) {
            rawType = (Class) ((ParameterizedType) type).getRawType();
        } else if (type instanceof Class) {
            rawType = (Class) type;
        } else {
            throw new RuntimeException("Not support type: " + type);
        }

        //Проверяем на "иерархию" через классы
        if (rawType == Class.class) {
            Type iGenericType = ((ParameterizedType) type).getActualTypeArguments()[0];
            return getGraphQLType(iGenericType);
        }

        //Проверяем на скалярный тип объекта
        GraphQLTypeScalar graphQLTypeScalar = graphQLSchemaType.getTypeScalarByClass(rawType);
        if (graphQLTypeScalar != null) return graphQLTypeScalar.getName();

        //Проверяем на коллекцию
        if (rawType == ArrayList.class || rawType == HashSet.class) {
            String genericTypeName = ((ParameterizedType) type).getActualTypeArguments()[0].getTypeName();
            Class clazzGenericType = Class.forName(genericTypeName, true, Thread.currentThread().getContextClassLoader());
            return "collection:" + getGraphQLType(clazzGenericType);
        }

        //Проверяем на GOptional
        if (rawType == GOptional.class) {
            Type iGenericType = ((ParameterizedType) type).getActualTypeArguments()[0];
            return getGraphQLType(iGenericType);
        }

        //Проверяем на GSubscribeEvent
        if (rawType == GSubscribeEvent.class) {
            Type iGenericType = ((ParameterizedType) type).getActualTypeArguments()[0];
            return getGraphQLType(iGenericType);
        }

        //Проверяем на input объект
        com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeInput aGraphQLTypeInput = (com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeInput) rawType.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeInput.class);
        if (aGraphQLTypeInput != null) return aGraphQLTypeInput.value();

        //Проверяем на out объект
        com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject aGraphQLType = (com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject) rawType.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject.class);
        if (aGraphQLType != null) return aGraphQLType.value();

        //Проверяем на union объект
        GraphQLTypeOutObjectInterface aGraphQLTypeOutUnion = (GraphQLTypeOutObjectInterface) rawType.getAnnotation(GraphQLTypeOutObjectInterface.class);
        if (aGraphQLTypeOutUnion != null) return aGraphQLTypeOutUnion.value();

        //Проверяем принадлежность к кастомным полям
        if (graphQLSchemaType.prepareCustomFields != null) {
            PrepareCustomField prepareCustomField = checkPrepareField(graphQLSchemaType, rawType);
            if (prepareCustomField != null) {
                return getGraphQLType(prepareCustomField.getEndType(type));
            }
        }

        throw new RuntimeException("Not support type: " + type);
    }

    /**
     * Возврощаем внешнее имя
     *
     * @param field
     * @return
     */
    private static String getGraphQLFieldName(Field field) {
        com.fuzzy.main.cluster.graphql.anotation.GraphQLField aGraphQLField = field.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLField.class);
        String graphQLName = aGraphQLField.value();
        if (graphQLName.trim().length() == 0) {
            graphQLName = GraphQLSchemaType.convertToGraphQLName(field.getName());
        }
        return graphQLName;
    }

    /**
     * Возврощаем внешнее имя
     *
     * @param method
     * @return
     */
    private static String getGraphQLFieldName(Method method) {
        com.fuzzy.main.cluster.graphql.anotation.GraphQLField aGraphQLField = method.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLField.class);
        String graphQLFieldName = aGraphQLField.value();
        if (graphQLFieldName.trim().length() == 0) {
            graphQLFieldName = method.getName();
            if (graphQLFieldName.startsWith("get") || graphQLFieldName.startsWith("set")) {
                graphQLFieldName = graphQLFieldName.substring(3);
            } else if (graphQLFieldName.startsWith("is")) {
                graphQLFieldName = graphQLFieldName.substring(2);
            }
            graphQLFieldName = GraphQLSchemaType.convertToGraphQLName(graphQLFieldName);
        }
        return graphQLFieldName;
    }

    /**
     * Возврощаем информацию о Deprecated
     *
     * @param field
     * @return
     */
    private static String getGraphQLFieldDeprecated(Field field) {
        com.fuzzy.main.cluster.graphql.anotation.GraphQLField aGraphQLField = field.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLField.class);
        String deprecated = aGraphQLField.deprecated();
        if (deprecated.trim().length() == 0) {
            deprecated = null;
        }
        return deprecated;
    }

    private static String getGraphQLFieldDeprecated(Method method) {
        com.fuzzy.main.cluster.graphql.anotation.GraphQLField aGraphQLField = method.getAnnotation(com.fuzzy.main.cluster.graphql.anotation.GraphQLField.class);
        String deprecated = aGraphQLField.deprecated();
        if (deprecated.trim().length() == 0) {
            deprecated = null;
        }
        return deprecated;
    }

    private static PrepareCustomField checkPrepareField(GraphQLSchemaType graphQLSchemaType, Class clazz) {
        if (graphQLSchemaType.prepareCustomFields != null) {
            for (PrepareCustomField customField : graphQLSchemaType.prepareCustomFields) {
                if (customField.isSupport(clazz)) return customField;
            }
        }
        return null;
    }

    /**
     * Вытаскиваем у класса всех родителей - union
     *
     * @param classRTypeGraphQL
     * @param unionGraphQLTypeNames
     */
    private static void findUnionGraphQLTypeNames(Class classRTypeGraphQL, Set<String> unionGraphQLTypeNames) {
        GraphQLTypeOutObjectInterface aGraphQLTypeOutObjectUnion = (GraphQLTypeOutObjectInterface) classRTypeGraphQL.getAnnotation(GraphQLTypeOutObjectInterface.class);
        if (aGraphQLTypeOutObjectUnion != null) {
            unionGraphQLTypeNames.add(aGraphQLTypeOutObjectUnion.value());
        }

        for (Class iClass : classRTypeGraphQL.getInterfaces()) {
            GraphQLTypeOutObjectInterface iAGraphQLTypeOutObjectUnion = (GraphQLTypeOutObjectInterface) iClass.getAnnotation(GraphQLTypeOutObjectInterface.class);
            if (iAGraphQLTypeOutObjectUnion == null) continue;
            unionGraphQLTypeNames.add(iAGraphQLTypeOutObjectUnion.value());
        }

        Class superClass = classRTypeGraphQL.getSuperclass();
        if (superClass == null) return;

        findUnionGraphQLTypeNames(superClass, unionGraphQLTypeNames);
    }
}
