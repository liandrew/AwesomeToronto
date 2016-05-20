package ca.liandrew.awesometoronto.firebase;

import com.firebase.client.Firebase;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by liandrew on 2016-05-06.
 */
public class FirebaseDatabase extends Firebase implements Serializable {

    FirebaseDatabase(String app_name){
        super(app_name);
    }

    public void logout(){
        if(getAuth()!=null){
            unauth();
        }
    }

    public boolean isAuthenticated(){
        return (getAuth()==null ? false : true);
    }

    public String getName(){
        String email = getEmail();
        String[] split = email.split("@");
        return split[0];
    }

    public String getEmail(){
        Map<String, Object> providerData = getProviderData();
        if (providerData!=null && providerData.containsKey("email")) {
            return providerData.get("email").toString();
        }
        return "";
    }

    private Map<String, Object> getProviderData(){
        if(getAuth()!=null){
            return (getAuth().getProviderData()!=null ? getAuth().getProviderData() : null);
        }
        return null;
    }

}
