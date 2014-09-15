package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.mvc.Content;
import play.mvc.Controller;

import java.io.File;
import java.io.InputStream;

/**
 * Created by keith on 9/14/2014.
 */
public class ApiController extends Controller {
    /**
     * Generates a 200 OK simple result.
     */
    public static Status ok() {
        response().setHeader("Access-Control-Allow-Origin", "*");
        response().setHeader(CACHE_CONTROL, "max-age=0");
        return Controller.ok();
    }

    /**
     * Generates a 200 OK simple result.
     */
    public static Status ok(Content content) {
        response().setHeader("Access-Control-Allow-Origin", "*");
        response().setHeader(CACHE_CONTROL, "max-age=0");
        return Controller.ok(content);
    }

    /**
     * Generates a 200 OK simple result.
     */
    public static Status ok(Content content, String charset) {
        response().setHeader("Access-Control-Allow-Origin", "*");
        response().setHeader(CACHE_CONTROL, "max-age=0");
        return Controller.ok(content, charset);
    }

    /**
     * Generates a 200 OK simple result.
     */
    public static Status ok(String content) {
        response().setHeader("Access-Control-Allow-Origin", "*");
        response().setHeader(CACHE_CONTROL, "max-age=0");
        return Controller.ok(content);
    }

    /**
     * Generates a 200 OK simple result.
     */
    public static Status ok(String content, String charset) {
        response().setHeader("Access-Control-Allow-Origin", "*");
        response().setHeader(CACHE_CONTROL, "max-age=0");
        return Controller.ok(content, charset);
    }

    /**
     * Generates a 200 OK simple result.
     */
    public static Status ok(ObjectNode content) {
        response().setHeader("Access-Control-Allow-Origin", "*");
        response().setHeader(CACHE_CONTROL, "max-age=0");
        return Controller.ok(content);
    }

    /**
     * Generates a 200 OK simple result.
     */
    public static Status ok(JsonNode content) {
        response().setHeader(CACHE_CONTROL, "max-age=0");
        return Controller.ok(content);
    }

    /**
     * Generates a 200 OK simple result.
     */
    public static Status ok(JsonNode content, String charset) {
        response().setHeader(CACHE_CONTROL, "max-age=0");
        return Controller.ok(content, charset);
    }



    /**
     * Generates a 200 OK simple result.
     */
    public static Status ok(byte[] content) {
        response().setHeader("Access-Control-Allow-Origin", "*");
        response().setHeader(CACHE_CONTROL, "max-age=0");
        return Controller.ok(content);
    }

    /**
     * Generates a 200 OK chunked result.
     */
    public static Status ok(InputStream content) {
        response().setHeader(CACHE_CONTROL, "max-age=0");
        return Controller.ok(content);
    }

    /**
     * Generates a 200 OK chunked result.
     */
    public static Status ok(InputStream content, int chunkSize) {
        response().setHeader(CACHE_CONTROL, "max-age=0");
        return Controller.ok(content, chunkSize);
    }

    /**
     * Generates a 200 OK chunked result.
     */
    public static Status ok(File content) {
        response().setHeader("Access-Control-Allow-Origin", "*");
        response().setHeader(CACHE_CONTROL, "max-age=0");
        return Controller.ok(content);
    }

    /**
     * Generates a 200 OK simple result.
     */
    public static Status ok(File content, int chunkSize) {
        response().setHeader("Access-Control-Allow-Origin", "*");
        response().setHeader(CACHE_CONTROL, "max-age=0");
        return Controller.ok(content, chunkSize);
    }

    /**
     * Generates a 200 OK chunked result.
     */
    public static Status ok(Chunks<?> chunks) {
        response().setHeader("Access-Control-Allow-Origin", "*");
        response().setHeader(CACHE_CONTROL, "max-age=0");
        return Controller.ok(chunks);
    }

}
