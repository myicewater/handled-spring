package servlet;

import antation.JAutowired;
import antation.JController;
import antation.JRequestMapping;
import antation.JService;
import common.MethodInvockHelper;
import util.PropUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;

public class DispatcherServlet extends HttpServlet {

    Properties properties = new Properties();

    Set<String> classes = new HashSet<>();

    Map<String,Object> beanMap = new Hashtable<>();
    Map<String,MethodInvockHelper> methodMapping = new Hashtable<>();
    Map<Class<?>,List<Object>> toInitFields = new Hashtable<>();



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("开始处理请求。");
        String contextPath1 = req.getContextPath(); //""
        String requestURI = req.getRequestURI(); //  /hello/hi
        StringBuffer requestURL = req.getRequestURL(); //  http://localhost:8989/hello/hi
        String contextPath = req.getServletPath();// ""

        if(requestURI.startsWith("/")){
            Map parameterMap = req.getParameterMap();
            MethodInvockHelper methodInvockHelper = methodMapping.get(requestURI.substring(1));
            Class<?> returnType = methodInvockHelper.getMethod().getReturnType();

            String invoke = null;
            try {
                Object o = methodInvockHelper.getcInstance();
                invoke = (String)methodInvockHelper.getMethod().invoke(o);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            ServletOutputStream outputStream = resp.getOutputStream();
            outputStream.print(invoke);

        }

        System.out.println("完成处理请求。");
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        String applicationContext = config.getInitParameter("applicationContext");
        System.out.println("Application init ");
        loadProperties(applicationContext);

        scanCompment(properties.getProperty("basePackege"));
        createBeans();
        System.out.println("初始化完成");


    }

    public void doDispatcher(){

    }

    public void createBeans() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        for(String name : classes){

            Class<?> aClass = null;
            try {
                aClass = Class.forName(name, true, classLoader);
//                Object o = aClass.newInstance();
//                Class<?> aClass1 = o.getClass();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            System.out.println("类名："+name);
            JController ac = aClass.getAnnotation(JController.class);
            if( ac != null){//控制器
                JRequestMapping ar = aClass.getAnnotation(JRequestMapping.class);
                String cm = ar.value();//class mapping

                Object o = null;//instance
                try {
                    o = aClass.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                Method[] declaredMethods = aClass.getDeclaredMethods();
                for(Method m:declaredMethods ){
                    JRequestMapping am = m.getAnnotation(JRequestMapping.class);
                    if(am != null){//映射
                        String mm = am.value();
                        MethodInvockHelper mh = new MethodInvockHelper(aClass,o,m);
                        methodMapping.put(cm+"/"+mm,mh);
                    }
                }


                Field[] declaredFields = aClass.getDeclaredFields();
                for(Field f: declaredFields){
                    JAutowired aa = f.getAnnotation(JAutowired.class);
                    if(aa != null){//注入
                        String fn = f.getName();
                        System.out.println("注入属性name:"+fn);
                        Object fb = beanMap.get(f.getName());
                        if(fb != null){//接口有实例
                            f.setAccessible(true);
                            try {
                                f.set(aClass,fb);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }else{//接口实现未初始化

                            List<Object> classes = toInitFields.get(f.getType());
                            if(classes == null){
                                classes = new ArrayList<Object>();
                                classes.add(o);
                                toInitFields.put(f.getType(),classes);
                            }else{
                                classes.add(o);
                            }
                        }
                    }
                }
                //beanMap.put("")

            }

            JService as = aClass.getAnnotation(JService.class);
            if(as != null){
                String cm = as.value();//class mapping
                Object o = null;
                try {
                    o = aClass.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                beanMap.put(cm,o);
                Type[] interfaces = aClass.getGenericInterfaces();
                if(interfaces != null && interfaces.length >0){
                    List<Object> objects = toInitFields.get(interfaces[0]);
                    if(objects != null && objects.size() >0){
                        for(Object ob:objects){
                            Class<?> oc = ob.getClass();
                            Field declaredField = null;
                            try {
                                declaredField = oc.getDeclaredField(cm);
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            }
                            declaredField.setAccessible(true);
                            try {
                                declaredField.set(ob,o);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    toInitFields.remove(interfaces[0]);

                }
                

            }






//            Annotation[] annotations = aClass.getAnnotations();
//            List<Annotation> annotations1 = Arrays.asList(annotations);
//
//            for(Annotation annotation : annotations){
//                if(annotation instanceof JController){//控制器
//                    JRequestMapping annotation1 = aClass.getAnnotation(JRequestMapping.class);
//                }
//                System.out.println(annotation.toString());
//            }

        }
    }

    public ClassLoader getDefaultClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }



    //扫描所有的类
    public void scanCompment(String path){
        String s = path.replaceAll("\\.", "/");
        URL resource = this.getClass().getClassLoader().getResource(s);
        File file = new File(resource.getFile());
        File[] files = file.listFiles();
        for(File f:files){
            if(f.isDirectory()){
                scanCompment(path+"."+f.getName());
            }else{
                String name = f.getName().replace(".class","");
                classes.add(path+"."+name);
//                System.out.println("scan class:"+path+name);
            }
        }


//        System.out.println("file:"+file);

    }

    //加载配置文件
    public void loadProperties(String applicationContext){

        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(applicationContext);
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String basePackege = properties.getProperty("basePackege");
        System.out.println("扫描路径"+basePackege);

//        PropUtil propUtil = new PropUtil();
//        System.out.println("util:"+propUtil.getPropertyByKey("basePackege"));
    }
}
