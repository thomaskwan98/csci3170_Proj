# JDBCproject
{{ member.user }} - {{ member.name }}
![image]({{ member.image }})
{{ member.content | markdownify }}

{% endfor %}

Last updated: {{ site.time }}

# how to run
```
javac Test.java
java -cp .:mysql-connector-java-5.1.47.jar Test
```
