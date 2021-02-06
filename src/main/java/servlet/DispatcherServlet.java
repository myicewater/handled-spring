package servlet;

import util.PropUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class DispatcherServlet extends HttpServlet {

    Properties properties = new Properties();

    Set<String> classes = new HashSet<>();

    Map<String,Object> beans = new Hashtable<>();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);



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


    }

    public void createBeans() throws ClassNotFoundException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        for(String name : classes){

            Class<?> aClass = Class.forName(name, true, classLoader);
            if(!aClass.isInterface()){
                beans.put()
            }
        }
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
                System.out.println("scan class:"+path+name);
            }
        }


        System.out.println("file:"+file);

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
