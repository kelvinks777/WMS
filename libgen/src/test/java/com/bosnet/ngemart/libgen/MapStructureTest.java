package com.bosnet.ngemart.libgen;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by bintang on 4/10/2017.
 */

public class MapStructureTest {

    @Test
    public void testGetList() {
        try {
            String passValue = "http://maps.googleapis.com/maps/api/geocode/json?latlng=-6.23799741520962,106.84735044836998";
            RestClient restClient = new RestClient();
            Example example = restClient.get(Example.class, passValue);
            if(example == null)
                Assert.assertFalse(true);
            else
                GetPostalCode(example);


        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.assertFalse(true);
        }
    }

    private void GetPostalCode(Example example){
        if(example.results.size() > 0){
            for(Result result : example.results){
                if(result.types.get(0).equalsIgnoreCase("postal_code")){
                    GetPostalCode(result.address_components);
                }
            }
        }
    }

    private void GetPostalCode(List<AddressComponent> addressComponentList){
        if(addressComponentList.size() > 0){
            for (AddressComponent component :  addressComponentList){
                if(component.types.get(0).equals("postal_code")){
                    String postalCode = component.long_name;
                }
            }
        }
    }

    public class AddressComponent
    {
        public String long_name;
        public String short_name;
        public List<String> types;
    }

    public class Northeast
    {
        public double lat;
        public double lng;
    }

    public class Southwest
    {
        public double lat;
        public double lng;
    }

    public class Bounds
    {
        public Northeast northeast;
        public Southwest southwest;
    }

    public class Location
    {
        public double lat;
        public double lng;
    }

    public class Viewport
    {
        public Northeast northeast;
        public Southwest southwest;
    }

    public class Geometry
    {
        public Bounds bounds;
        public Location location;
        public String location_type;
        public Viewport viewport;
    }

    public class Result
    {
        public List<AddressComponent> address_components;
        public String formatted_address;
        public Geometry geometry;
        public String place_id;
        public List<String> types;
    }

    public class Example
    {
        public List<Result> results;
        public String status;
    }

}
