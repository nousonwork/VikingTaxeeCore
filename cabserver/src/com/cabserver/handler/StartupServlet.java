package com.cabserver.handler;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.cabserver.util.CacheBuilder;



/**
 * Servlet implementation class StartupServlet
 */
public class StartupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StartupServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		
		//final Logger log = Logger.getLogger("cabserverlogger");
		
		final Logger log = Logger.getLogger(
				com.cabserver.handler.StartupServlet.class.getName());
		
		/*System.getProperties().put("http.proxyHost", "rtecproxy.ril.com");
		System.getProperties().put("http.proxyPort", "8080");
		System.getProperties().put("http.proxyUser", "shankar.mohanty");
		System.getProperties().put("http.proxyPassword", "cu141#123");*/
				
		log.info("Building Cache");
		CacheBuilder.buildCache();
		log.info("Cache built.");
		
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
