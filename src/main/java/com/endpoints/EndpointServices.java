package com.endpoints;
import io.micronaut.http.annotation.*;
import java.util.List;


@Controller("/ids")
public class EndpointServices {



    @Get
    public List<Integer> getIds() {
        return List.of(101, 201, 301);
    }




}
