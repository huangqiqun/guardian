package com.leadbank.guardian.integration.utils;

/**
 * 
 *  利得金融
 * Copyright (c) 2013-2015 leadbank,Inc.All Rights Reserved.
 */

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.util.DigestUtils;


/**
 * Bean 对象属性copy Util
 * 
 * @author JASON
 * @version $Id: ConvertBeanUtil.java, v 0.1 2015年5月26日 上午10:43:12 JASON Exp $
 */
public class ConvertBeanUtil {
    
    private static ConcurrentHashMap<String,BeanCopier> cache=new ConcurrentHashMap<String, BeanCopier>();
    

    /**
     * @param source 源对象class
     * @param target 目标对对象class
     * @param sourceObj 复制的源对象
     * @param useConverter 
     * @return 
     * @throws Exception
     */
    public static <T> T copyBeanProperties(@SuppressWarnings("rawtypes") Class source,Class<T> target,Object sourceObj, boolean useConverter) {
        if(sourceObj==null) return null;
        T t;
        try {
            t = target.newInstance();
        } catch (Exception e) {
            return null;
        }
        String key=source.getSimpleName()+target.getSimpleName();
        BeanCopier copier = cache.get(key);
        if(copier==null){
             copier=createBeanCopier(source, target, useConverter, key);
        }
        copier.copy(sourceObj, t, null);
        return t;
    }
    /**
     * 
     * 
     * @param sourceObj  源对象
     * @param target  目标对象
     * @param useConverter 
     * @return
     * @throws Exception
     */
    public static <T> T copyBeanProperties(Object sourceObj, T target){
        return copyBeanProperties(sourceObj, target, false);
    }
    /**
     * 
     * 
     * @param sourceObj  源对象
     * @param target  目标对象
     * @param useConverter 
     * @return
     * @throws Exception
     */
    public static <T> T copyBeanProperties(Object sourceObj, T target, boolean useConverter) {
        if(sourceObj==null||target==null) return null;
        String key=sourceObj.getClass().getSimpleName()+target.getClass().getSimpleName();
        BeanCopier copier = cache.get(key);
        if(copier==null){
             copier=createBeanCopier(sourceObj.getClass(), target.getClass(), useConverter, key);
        }
        copier.copy(sourceObj, target, null);
        return target;
    }
    
    public static <T> List<T> copyListBeanPropertiesToList(List<?> sourceObjs,List<T> targets,Class<T> targetType){
        if(sourceObjs==null||targets==null||targetType==null) return null;
        T t;
        for(Object o:sourceObjs){
            try {
                t=targetType.newInstance();
                targets.add(copyBeanProperties(o,t,false));
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return targets;
    }
    
    @SuppressWarnings("unused")
    private static String getHashKey(String str){
        if(str==null) return null;
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }
    
    @SuppressWarnings({"rawtypes" })
    private static BeanCopier createBeanCopier(Class sourceClass,Class targetClass,boolean useConverter,String cacheKey){
        BeanCopier copier = BeanCopier.create(sourceClass,targetClass, useConverter);
        cache.putIfAbsent(cacheKey, copier);
        return copier;
    }    
}
