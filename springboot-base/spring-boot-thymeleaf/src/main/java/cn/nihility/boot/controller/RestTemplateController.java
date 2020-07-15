package cn.nihility.boot.controller;

import cn.nihility.boot.model.Foo;
import org.springframework.web.bind.annotation.*;

/**
 * RestController
 *
 * @author dandelion
 * @date 2020-04-12 10:59
 */
@RestController
@RequestMapping("/rest")
public class RestTemplateController {

    @RequestMapping(value = {"/hei"}, method = RequestMethod.GET)
    public String hei() {
        return "hei";
    }

    @RequestMapping(value = "/foo", method = RequestMethod.GET)
    public Foo getFool(@RequestParam(name = "id", defaultValue = "10000") Long id,
                       @RequestParam(name = "name", defaultValue = "default Name") String name) {
        return new Foo(id, name);
    }

    @RequestMapping(value = "/foo-header", method = RequestMethod.GET)
    public Foo getFooFormHeader(@RequestHeader(name = "id", defaultValue = "-1") Integer id,
                                @RequestHeader(name = "name", defaultValue = "dn") String name) {
        return new Foo(id, name);
    }

    @RequestMapping(value = "/foo2/{id}/{name}", method = RequestMethod.GET)
    public Foo getFool2(@PathVariable(name = "id") Long id,
                       @PathVariable(name = "name") String name) {
        return new Foo(id, name);
    }


    @RequestMapping(value = "/foo2", method = RequestMethod.POST)
    public Foo fooPost(@RequestBody Foo foo) {
        return foo;
    }

    @RequestMapping(value = "/foo21", consumes = "application/json;charset=utf-8", produces = "application/json", method = RequestMethod.POST)
    public Foo fooPost2(@RequestBody Foo foo,
                        @RequestHeader(name = "h1", defaultValue = "default h1 value") String h1,
                        @RequestHeader(name = "h2", defaultValue = "default h2value") String h2) {
        return new Foo(foo.getId(), foo.getName(), h1 + ":" + h2);
    }

}
