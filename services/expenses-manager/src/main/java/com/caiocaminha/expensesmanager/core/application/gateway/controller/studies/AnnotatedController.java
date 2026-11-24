package com.caiocaminha.expensesmanager.core.application.gateway.controller.studies;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // composed annotation of @Controller and @ResponseBody
@RequestMapping("v1/testing")
public class AnnotatedController {

    @GetMapping("/text/{name}") // @RequestMapping(method = RequestMethod.GET)
    public String getText(@PathVariable String name) { //no need to specify path name if you compile your code with -parameters compiler flag.
        return "hello world";
    }


}
