# Code

``` c
{% include_relative code.c %}
```

# Contributors

- loookforanans
- lalalam123
- ColsonCZH
- viscory
- byydzh
- JunboShen
- ???
{% for stu in site.stu %}
  <h2>
    <a href="https://github.com/{{ stu.user }}">
      {{ stu.user }} - {{ stu.name }}
    </a>
  </h2>
  ![image]({{ stu.image }})
  <p>{{ stu.content | markdownify }}</p>
{% endfor %}

Last updated: {{ site.time }}
# Workflow

# how to run
```
javac Test.java
java -cp .:mysql-connector-java-5.1.47.jar Test
```
