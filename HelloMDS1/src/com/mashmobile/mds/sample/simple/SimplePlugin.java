package com.mashmobile.mds.sample.simple;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import com.mashmobile.mds.library.plugin.BadRequestException;
import com.mashmobile.mds.library.plugin.InternalServerErrorException;
import com.mashmobile.mds.library.plugin.Plugin;


public class SimplePlugin extends Plugin {

	private final static String pluginVersion = "1.0.0";
	private final static String pluginName = "Simple Plugin";
	private final static String pluginURI =
		"/sample/hello*";
	private final static String pluginID = "/sample/hello";

	public SimplePlugin() {
		super(pluginVersion, pluginName, pluginID);
	}

	public void doHandle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException,
			BadRequestException, InternalServerErrorException {
		response.setStatusCode(HttpStatus.SC_OK);
		StringEntity entity = new StringEntity(
				"<html>\n" +
				"<head>\n" +
				"<title>Hello MDS</title>\n" +
				"<body>\n" +
				"<h1>Hello MDS</h1>\n" +
				"</body>\n" +
				"</html>",
				"UTF-8"
		);
		entity.setContentType("text/html; charset=utf-8");
		entity.setContentEncoding("UTF-8");
		response.setEntity(entity);
	}
	
	public Set<String> uriPatterns() {
		return Collections.singleton(pluginURI);
	}

	public void dispose() {}
	
}
