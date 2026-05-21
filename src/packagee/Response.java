/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee;

/**
 *
 * @author juand
 */
public class response {

    public static final int SUCCESS        = 200;
    public static final int BAD_REQUEST    = 400;
    public static final int UNAUTHORIZED   = 401;
    public static final int NOT_FOUND      = 404;
    public static final int CONFLICT       = 409;
    public static final int ERROR          = 500;   
    private final int statusCode;
    private final String message;
    private final Object data;  

    public response(int statusCode, String message, Object data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public response(int statusCode, String message) {
        this(statusCode, message, null);
    }

    public int getStatusCode(){
        return statusCode;
    }
    public String getMessage(){
        return message;
    }
    public Object getData(){ 
        return data;
    }

    public boolean isSuccess() {
        return statusCode == SUCCESS;
    }
}
