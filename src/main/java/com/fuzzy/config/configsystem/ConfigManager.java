package com.fuzzy.config.configsystem;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.exception.runtime.ConfigBuilderException;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.util.Optional;
import java.util.Set;

/**
 * Created by a.kiperku
 * Date: 04.04.2024
 */

public class ConfigManager {

    public static void loadAll(){
        Reflections reflections = new Reflections("com.fuzzy.config");
        final Set<Class<?>> configClasses = reflections.getTypesAnnotatedWith(ConfigClass.class);
        configClasses.forEach(aClass ->{
            ConfigBuilder.load(aClass, false);
            System.out.println("ConfigSystem: Loaded " + aClass.getSimpleName());
        });
    }

    public static void loadByName(String name){
        Reflections reflections = new Reflections("com.fuzzy.config");
        final Set<Class<?>> configClazzes = reflections.getTypesAnnotatedWith(ConfigClass.class);

        final Optional<Class<?>> optionalClass = configClazzes.stream()
                .filter(aClass -> aClass.getAnnotation(ConfigClass.class).name().equals(name))
                .findFirst();

        if (optionalClass.isEmpty()){
            throw new ConfigBuilderException(name + " config not found");
        }
        ConfigBuilder.load(optionalClass.get(), false);
        System.out.println("ConfigSystem: Loaded " + optionalClass.get().getSimpleName());
    }




}
