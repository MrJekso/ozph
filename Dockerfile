FROM openjdk:23
COPY app/Main.jar .
EXPOSE 9999
ENTRYPOINT ["java","-jar","./Main.jar"]
