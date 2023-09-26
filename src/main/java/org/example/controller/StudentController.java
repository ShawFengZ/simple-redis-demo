package org.example.controller;

import org.example.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class StudentController {

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/student")
    public void addStudent(@RequestBody Student student) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("student", student);
    }

    @ResponseBody
    @GetMapping("/student/{id}")
    public Student getStudent(@PathVariable String id) {
        Student student = (Student)redisTemplate.opsForValue().get(id);
        return student;
    }

    @RequestMapping("/string")
    public String stringTest() {
        redisTemplate.opsForValue().set("str", "hello world");
        return (String) redisTemplate.opsForValue().get("str");
    }

    @RequestMapping("/list")
    public List<String> listTest() {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.leftPush("list", "Hello");
        listOperations.leftPush("list", "World");

        List<String> list = listOperations.range("list", 0, 1);
        return list;
    }

    @RequestMapping("/set")
    public Set<String> setTest() {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        setOperations.add("set", "Hello");
        setOperations.add("set", "Hello");
        setOperations.add("set", "java");

        return setOperations.members("set");
    }

    @RequestMapping("/zset")
    public Set<String> zsetTest() {
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add("zset", "Hello", 1);
        zSetOperations.add("zset", "world", 2);
        zSetOperations.add("zset", "world", 3);

        return zSetOperations.range("zset", 0, 2);
    }

//    @RequestMapping
//    public Map<String, String> mapTest() {
//        HashOperations hashOperations = redisTemplate.opsForHash();
//        hashOperations.put
//    }
}
