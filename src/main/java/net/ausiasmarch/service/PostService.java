package net.ausiasmarch.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import net.ausiasmarch.bean.PostBean;
import net.ausiasmarch.bean.ResponseBean;
import net.ausiasmarch.connection.ConnectionInterface;
import net.ausiasmarch.dao.PostDao;
import net.ausiasmarch.factory.ConnectionFactory;
import net.ausiasmarch.setting.ConnectionSettings;

public class PostService {

    HttpServletRequest oRequest = null;

    public PostService(HttpServletRequest oRequest) {
        this.oRequest = oRequest;
    }

    public String get() throws SQLException {
        ConnectionInterface oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
        Connection oConection = oConnectionImplementation.newConnection();
        int id = Integer.parseInt(oRequest.getParameter("id"));
        PostDao oPostDao = new PostDao(oConection);
        PostBean oPostBean = oPostDao.get(id);
        Gson oGson = new Gson();
        String strJson = oGson.toJson(oPostBean);
        oConnectionImplementation.disposeConnection();
        return "{\"status\":200,\"response\":" + strJson + "}";
    }

    public String update() throws SQLException {
        ConnectionInterface oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
        Connection oConnection = oConnectionImplementation.newConnection();
        Gson oGson = new Gson();
        oGson = new GsonBuilder().setDateFormat("dd/MMM/yyyy HH:mm").create();
        ResponseBean oResponseBean;
        PostBean oPostBean = new PostBean();

        String data = oRequest.getParameter("data");
        oPostBean = oGson.fromJson(data, PostBean.class);

        PostDao oPostDao = new PostDao(oConnection);

        if (oPostDao.update(oPostBean) == 0) {
            oResponseBean = new ResponseBean(500, "KO");
        } else {
            oResponseBean = new ResponseBean(200, "OK");
        }

        oConnectionImplementation.disposeConnection();
        return oGson.toJson(oResponseBean);
    }

    public String getAll() throws SQLException {
        ConnectionInterface oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
        Connection oConection = oConnectionImplementation.newConnection();
        PostDao oPostDao = new PostDao(oConection);
        Gson oGson = new Gson();
        String message = "";

       // oGson = new GsonBuilder().setDateFormat("dd/MMM/yyyy HH:mm").create();
        List<PostBean> listaPostBean = oPostDao.getall();
        if (listaPostBean == null) {
            message = "\"La lista está vacia\"";
        } else {
            //oGson = gh.getGson();
            message = oGson.toJson(listaPostBean);
        }
        oConnectionImplementation.disposeConnection();
        return "{\"status\":200,\"response\":" + message + "}";
    }

    public String insert() throws SQLException {
        ConnectionInterface oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
        Connection oConection = oConnectionImplementation.newConnection();

        //int id = Integer.parseInt(oRequest.getParameter("id"));
        //PostBean oPostBean = new PostBean();
        final GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson oGson = builder.create();

        oGson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
        PostBean oPostBean = oGson.fromJson(oRequest.getParameter("data"), PostBean.class);

        ResponseBean oResponseBean;
        PostDao oPostDao = new PostDao(oConection);
        if (oPostDao.insert(oPostBean) == 0) {
            oResponseBean = new ResponseBean(500, "KO");
        } else {
            oResponseBean = new ResponseBean(200, "OK");
        };
        oConnectionImplementation.disposeConnection();
        return oGson.toJson(oResponseBean);

    }

    public String fill() throws SQLException {
        ConnectionInterface oConnectionImplementation = ConnectionFactory.getConnection(ConnectionSettings.connectionPool);
        Connection oConection = oConnectionImplementation.newConnection();

        Lorem lorem = LoremIpsum.getInstance();
        int limit = Integer.parseInt(oRequest.getParameter("limit"));

        String titulo,cuerpo,etiquetas;
        Gson oGson = new Gson();
        PostDao oPostDao = new PostDao(oConection);
        PostBean oPostBean;
        
        for (int i = 0; i < limit; i++) {
            titulo = lorem.getTitle(2, 4);
            cuerpo = lorem.getParagraphs(2, 4);
            etiquetas = lorem.getWords(5, 10);
            oPostBean = new PostBean(titulo, cuerpo, etiquetas);
            oPostDao.insert(oPostBean);
        }
        ResponseBean oResponseBean;
        oResponseBean = new ResponseBean(200, "Ok");    
        oConnectionImplementation.disposeConnection();
        return oGson.toJson(oResponseBean);

    }
}
