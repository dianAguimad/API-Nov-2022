import com.github.javafaker.Bool;
import com.github.javafaker.Faker;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jdk.jfr.Name;

import org.junit.jupiter.api.*;

import java.lang.reflect.Array;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class eCommerce {
    static private String url_base = "webapi.segundamano.mx";
    static private String email = "ventas1234568912@mailinator.com";
    static private String password = "123456";
    static private String account_id = "";
    static private String access_token = "";
    static private String uuid = "";
    static private String ad_id;
    static private String addressID = "";
    static private  String account_id_solo = "";
    static private String id_alerta = "";
    static private Faker faker = new Faker();
    static private String alias = "";
    static private String name = "";


    //String      getParameter (String nombre)

    @Name("Obtener token")
    private String obtener_Token(){

        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        String body_request = "{\"account\":{\"email\":\""+email+"\"}}";

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .auth().preemptive().basic(email,password)
                .queryParam("lang","es")
                .body(body_request)
                .post();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Response del token: " + body_response);

        JsonPath jsonResponse = response.jsonPath();

        access_token = jsonResponse.get("access_token");
        //pm.environment.set("token",pm.response.json().access_token)
        System.out.println("Token:"+ access_token);

        account_id = jsonResponse.get("account.account_id");
        System.out.println("Account ID: "+ account_id);

        uuid = jsonResponse.get("account.uuid");
        System.out.println("UUID: "+ uuid);

        return access_token;
    }

    @Name("Crear datos para una dirrecion")
    private String[] datos_direccion(){
          alias = faker.harryPotter().character();
          name = faker.name().fullName();
         String intArray[] = {name,alias};

        return intArray;
    }

    @Test
    @Order(1)
    @Severity(SeverityLevel.MINOR)
    @DisplayName(" 1 Test case: Validar el filtrado de categorias")
    public void obtener_Categorias_filtrado_200(){

        RestAssured.baseURI = String.format("https://%s/urls/v1/public",url_base);
        String body_request = "{\n" +
                "\t\"filters\": [{\n" +
                "\t\t\"price\": \"-60000\",\n" +
                "\t\t\"category\": \"2020\"\n" +
                "\t}, {\n" +
                "\t\t\"price\": \"60000-80000\",\n" +
                "\t\t\"category\": \"2020\"\n" +
                "\t}, {\n" +
                "\t\t\"price\": \"80000-100000\",\n" +
                "\t\t\"category\": \"2020\"\n" +
                "\t}, {\n" +
                "\t\t\"price\": \"100000-150000\",\n" +
                "\t\t\"category\": \"2020\"\n" +
                "\t}, {\n" +
                "\t\t\"price\": \"150000-\",\n" +
                "\t\t\"category\": \"2020\"\n" +
                "\t}]\n" +
                "}";


        Response response=given()
                .log().all()
                .filter(new AllureRestAssured())
                .queryParam("lang","es")
                .contentType("application/json")
                .headers("Accept","application/json, text/plain, */*")
                .body(body_request)
                .post("/ad-listing");

        String body_response = response.getBody().prettyPrint();
        //System.out.println("Response: " + body_response);

        //Junit 5 - Pruebas

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("urls"));

        //Validar que el body response contenga un filtro correcto
        assertTrue(body_response.contains("/anuncios/mexico/autos-y-camionetas?precio=-60000"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        //Validar tiempo de respuesta
        assertTrue(tiempo < 3000);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.TRIVIAL)
    @DisplayName(" 2 Test case: Listado de Anuncios")
    public void listado_anuncios_200(){
        RestAssured.baseURI = String.format("https://%s/highlights/v1",url_base);

        Response response=given()
                .log().all()
                .filter(new AllureRestAssured())
                .queryParam("prio",1)
                .queryParam("cat","2020")
                .queryParam("lim",1)
                .headers("Accept","*/*")
                .get("/public/highlights");

        String body_response = response.getBody().prettyPrint();
        System.out.println("Response: " + body_response);

        //Junit 5 - Pruebas

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("list_id"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 2500);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));
    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("3 Test case: Obtener información del usuario")
    public void obtener_info_usuario_200(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        String body_request = "{\"account\":{\"email\":\""+email+"\"}}";

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .auth().preemptive().basic(email,password)
                .queryParam("lang","es")
                .body(body_request)
                .post();

        String body_response = response.getBody().prettyPrint();
        //Junit 5 - Pruebas

        //validar el status code
        assertEquals(200,response.getStatusCode());

        //Validar que el campo email contiene el email esperado
        JsonPath jsonResponse = response.jsonPath();
        String emailResponse = jsonResponse.get("account.email");
        assertEquals(emailResponse, email);

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("account_id"));
        assertTrue(body_response.contains("token"));
        assertTrue(body_response.contains("uuid"));

        long tiempo = response.getTime();
        assertTrue(tiempo < 4500);

        //Validar que el header "Content-Type es de tipo application/json
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

        //Asignar valores a nuestras variables globales
        access_token = jsonResponse.get("access_token");
        account_id = jsonResponse.get("account.account_id");
        account_id_solo =  jsonResponse.get("account.account_id");
        account_id_solo = account_id_solo.split("/")[3];
        uuid = jsonResponse.get("account.uuid");

    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("4 Test case: Crear Usuario")
    public void crear_Usuario_401(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        String new_user = "agente_ventas_test" + (Math.floor(Math.random()*6789)) + "@mailinator.com";
        String new_password = "12345";
        String body_request = "{\"account\":{\"email\":\""+new_user+"\"}}";

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .auth().preemptive().basic(new_user,new_password)
                .queryParam("lang","es")
                .body(body_request)
                .post();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Response: " + body_response);

        //Junit 5 - Pruebas

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(401,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("ACCOUNT_VERIFICATION_REQUIRED"));
        assertTrue(body_response.contains("error"));

        System.out.println("Tiempo respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 1500);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(5)
    @Severity(SeverityLevel.MINOR)
    @DisplayName("5 Test case: Editar usuario")
    public void editar_datos_de_usuario_200(){

        //Datos random - fakersjs
        String name = faker.name().fullName();
        Boolean randomBoolean = faker.bool().bool();
        String phone = faker.phoneNumber().cellPhone();

        String body_request = "{\n" +
                "    \"account\": {\n" +
                "        \"name\": \""+name+"\",\n" +
                "        \"phone\": \"1234567890\",\n" +
                "        \"professional\": "+randomBoolean+",\n" +
                "        \"phone_hidden\": "+randomBoolean+"\n" +
                "    }\n" +
                "}";

        String token = obtener_Token();

        RestAssured.baseURI = String.format("https://%s/nga/api/v1%s?lang=es",url_base,account_id);

        Response response = given()
                .log().all()
                .contentType("application/json")
                .header("Accept","*/*")
                .header("Authorization","tag:scmcoord.com,2013:api " +token)
                .body(body_request)
                .patch();

        String body_response = response.getBody().prettyPrint();

        //Junit 5 - Pruebas

        //validar el status code
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("account_id"));
        assertTrue(body_response.contains("email"));
        assertTrue(body_response.contains("professional"));

        //Validar que el nombre sea el de la variable name
        JsonPath jsonResponse = response.jsonPath();
        String nameResponse = jsonResponse.get("account.name");
        assertEquals(nameResponse, name);

        //validar tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 3500);

        //Validar que los headers contengan application/json
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(6)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("6 Test case: Crear un Anuncio")
    public void crear_un_anuncio_200(){

        String productName = faker.commerce().productName();
        String productMaterial = faker.commerce().material();
        String productColor = faker.commerce().color();
        Integer productPrice = faker.number().randomDigitNotZero();
        String subject = productName + " "  + productMaterial + " " + productColor;

        String token = obtener_Token();

        String body_request = "{\n" +
                "    \"images\": \"2265766694.jpg\",\n" +
                "    \"category\": \"4100\",\n" +
                "    \"subject\": \""+subject+"\",\n" +
                "    \"body\": \"Compra y venta e intercambio de "+subject+"\",\n" +
                "    \"is_new\": \"0\",\n" +
                "    \"region\": \"12\",\n" +
                "    \"municipality\": \"316\",\n" +
                "    \"area\": \"69520\",\n" +
                "    \"price\": \""+productPrice+"\",\n" +
                "    \"phone_hidden\": \"true\",\n" +
                "    \"show_phone\": \"false\",\n" +
                "    \"contact_phone\": \"267-941-5539\"\n" +
                "}";

        RestAssured.baseURI = String.format("https://%s/v2/accounts/%s/up",url_base,uuid);
        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .header("x-source", "PHOENIX_DESKTOP")
                .header("Accept","*/*")
                .header("Content-type","application/json")
                .auth().preemptive().basic(uuid,token)
                .body(body_request)
                .post();

        String body_response = response.getBody().prettyPrint();
        //Junit 5 - Pruebas

        //validar el status code
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("ad_id"));
        assertTrue(body_response.contains("subject"));
        assertTrue(body_response.contains("category"));

        //Validar que el subject sea el mismo que se mando con las variables
        JsonPath jsonResponse = response.jsonPath();
        String subjectResponse = jsonResponse.get("data.ad.subject");
        assertEquals(subjectResponse, subject);

        long tiempo = response.getTime();
        assertTrue(tiempo < 4000);

        //Validar que los headers contengan application/json
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

        ad_id = jsonResponse.get("data.ad.ad_id");

    }

    @Test
    @Order(7)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("7 Test case: Ver un Anuncio")
    public void ver_un_anuncio_200(){

        RestAssured.baseURI = String.format("https://%s/ad-stats/v1/public/accounts/%s/ads/%s",url_base,account_id_solo,ad_id);
        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .header("Accept","*/*")
                .get();

        String body_response = response.getBody().prettyPrint();
        //Junit 5 - Pruebas

        //validar el status code
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("list_id"));
        assertTrue(body_response.contains("total"));

        //Validar que el list_id sea el mismo que la variable ad_id
        JsonPath jsonResponse = response.jsonPath();
        String list_idResponse = jsonResponse.get("list_id");
        assertEquals(list_idResponse, ad_id);

        long tiempo = response.getTime();
        assertTrue(tiempo < 2000);

        //Validar que los headers contengan application/json
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(8)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("8 Test case: Editar un Anuncio")
    public void editar_un_anuncio_200(){

        String productName = faker.commerce().productName();
        String productMaterial = faker.commerce().material();
        String productColor = faker.commerce().color();
        Integer productPrice = faker.number().randomDigitNotZero();
        String subject = productName + " "  + productMaterial + " " + productColor;

        String token = obtener_Token();

        String body_request = "{\n" +
                "    \"category\": \"8143\",\n" +
                "    \"subject\": \""+subject+"\",\n" +
                "    \"body\": \"Compra y venta e intercambio de "+subject+"\",\n" +
                "    \"region\": \"12\",\n" +
                "    \"municipality\": \"316\",\n" +
                "    \"area\": \"69520\",\n" +
                "    \"price\": \""+productPrice+"\",\n" +
                "    \"phone_hidden\": \"true\",\n" +
                "    \"show_phone\": \"false\",\n" +
                "    \"contact_phone\": \"76013183\"\n" +
                "}";

        RestAssured.baseURI = String.format("https://%s/v2/accounts/%s/up/%s",url_base,uuid,ad_id);
        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .header("x-source", "PHOENIX_DESKTOP")
                .header("Accept","*/*")
                .header("Content-type","application/json")
                .auth().preemptive().basic(uuid,token)
                .body(body_request)
                .put();

        String body_response = response.getBody().prettyPrint();
        //Junit 5 - Pruebas

        //validar el status code
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("ad_id"));
        assertTrue(body_response.contains("subject"));
        assertTrue(body_response.contains("category"));

        //Validar que el subject sea el mismo que se mando con las variables
        JsonPath jsonResponse = response.jsonPath();
        String subjectResponse = jsonResponse.get("data.ad.subject");
        assertEquals(subjectResponse, subject);

        //validar que el tiempo de respuesta sea menor a 5s
        long tiempo = response.getTime();
        assertTrue(tiempo < 5000);

        //Validar que los headers contengan application/json
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(9)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("9 Test case: Crear alerta")
    public void crear_Alerta_200(){
        String token = obtener_Token();

        String body_request = "{\n" +
                "    \"ad_listing_service_filters\": {\n" +
                "        \"region\": \"16\",\n" +
                "        \"category_lv0\": \"1000\",\n" +
                "        \"category_lv1\": \"1020\"\n" +
                "    }\n" +
                "}";

        RestAssured.baseURI = String.format("https://%s/alerts/v1/private/account/%s/alert",url_base,uuid);
        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .header("Accept","*/*")
                .header("Content-type","application/json")
                .auth().preemptive().basic(uuid,token)
                .body(body_request)
                .post();

        String body_response = response.getBody().prettyPrint();
        //Junit 5 - Pruebas

        //validar el status code
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("title"));
        assertTrue(body_response.contains("url_path"));

        //Validar que el titulo de la alerta sea Venta inmuebles en Jalisco
        JsonPath jsonResponse = response.jsonPath();
        String title = jsonResponse.get("data.alert.title");
        assertEquals(title, "Venta inmuebles en Jalisco");

        //validar que el tiempo de respuesta sea menor a 4s
        long tiempo = response.getTime();
        assertTrue(tiempo < 4000);

        //Validar que los headers contengan application/json
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

        id_alerta = jsonResponse.get("data.alert.id");

    }

    @Test
    @Order(10)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("10 Test case: Eliminar alerta")
    public void eliminar_Alerta_200(){
        String token = obtener_Token();

        RestAssured.baseURI = String.format("https://%s/alerts/v1/private/account/%s/alert/%s",url_base,uuid,id_alerta);
        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .header("Accept","*/*")
                .auth().preemptive().basic(uuid,token)
                .delete();

        String body_response = response.getBody().prettyPrint();
        //Junit 5 - Pruebas

        //validar el status code
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("status"));

        //Validar que el status sea "ok"
        JsonPath jsonResponse = response.jsonPath();
        String status = jsonResponse.get("data.status");
        assertEquals(status, "ok");

        //validar que el tiempo de respuesta sea menor a 4s
        long tiempo = response.getTime();
        assertTrue(tiempo < 4000);

        //Validar que los headers contengan application/json
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(11)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("11 Test case: Crear Direccion")
    public void crear_una_direccion_201(){

        String token = obtener_Token();
        String[] datosDireccion = datos_direccion();
        String randomNumber = faker.address().buildingNumber();


        RestAssured.baseURI = String.format("https://%s/addresses/v1/create",url_base);

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .formParam("contact",  datosDireccion[0])
                .formParam("phone", "1234567890")
                .formParam("rfc", "XAXX010101000")
                .formParam("zipCode", "11011")
                .formParam("exteriorInfo", randomNumber)
                .formParam("region", "16")
                .formParam("municipality", "670")
                .formParam("area","12652")
                .formParam("alias", datosDireccion[1])
                .contentType("application/x-www-form-urlencoded")
                .header("Accept","*/*")
                .auth().preemptive().basic(uuid,token)
                .post();

        String body_response = response.getBody().prettyPrint();
        //Junit 5 - Pruebas

        //validar el status code
        assertEquals(201,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //validar tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 3500);


        //Validar que el addressID tiene un tamaño de 36 caracteres
        JsonPath jsonResponse = response.jsonPath();
        addressID = jsonResponse.get("addressID");
        assertEquals(addressID.length(), 36);

    }

    @Test
    @Order(12)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("12 Test case: Ver Direccion")
    public void ver_direccion_200(){

        String token = obtener_Token();

        RestAssured.baseURI = String.format("https://%s/addresses/v1/get/%s",url_base,addressID);
        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .header("Accept","*/*")
                .auth().preemptive().basic(uuid,token)
                .get();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Response 1: " + body_response);
        //Junit 5 - Pruebas

        //validar el status code
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("addresses"));
        assertTrue(body_response.contains("alias"));

        //Validar que el nombre de la direccion es el valor que le mande al crearla
        JsonPath jsonResponse = response.jsonPath();
        String direcctionNombre = jsonResponse.get("addresses."+addressID+".contact");
        assertEquals(direcctionNombre, name);

        //Validar que el alias de la direccion es el valor que le mande al crearla
        String aliasDireccion = jsonResponse.get("addresses."+addressID+".alias");
        assertEquals(aliasDireccion, alias);

        //validar que el tiempo de respuesta sea menor a 2s
        long tiempo = response.getTime();
        assertTrue(tiempo < 2000);

        //Validar que los headers contengan application/json
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(13)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("13 Test case: Editar Direccion")
    public void editar_una_direccion_200(){

        String token = obtener_Token();
        String randomNumber = faker.address().buildingNumber();
        String[] datosDireccion = datos_direccion();

        RestAssured.baseURI = String.format("https://%s/addresses/v1/modify/%s",url_base,addressID);


        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .formParam("contact", datosDireccion[0])
                .formParam("phone", "1234567890")
                .formParam("rfc", "XAXX010101000")
                .formParam("zipCode", "11011")
                .formParam("exteriorInfo", randomNumber)
                .formParam("region", "16")
                .formParam("municipality", "670")
                .formParam("area","12652")
                .formParam("alias",datosDireccion[1])
                .contentType("application/x-www-form-urlencoded")
                .header("Accept","*/*")
                .auth().preemptive().basic(uuid,token)
                .put();

        String body_response = response.getBody().prettyPrint();
        //Junit 5 - Pruebas

        //validar el status code
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //validar tiempo de respuesta
        long tiempo = response.getTime();
        assertTrue(tiempo < 2000);

        //Validar que el texto del parametro "message"
        JsonPath jsonResponse = response.jsonPath();
        String message = jsonResponse.get("message");
        assertEquals(message, ""+addressID+" modified correctly");

    }

    @Test
    @Order(14)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("14 Test case: Ver Direccion despues de editar")
    public void ver_direccion_despues_de_editar_200(){

        String token = obtener_Token();

        RestAssured.baseURI = String.format("https://%s/addresses/v1/get/%s",url_base,addressID);
        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .header("Accept","*/*")
                .auth().preemptive().basic(uuid,token)
                .get();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Response 2: " + body_response);
        //Junit 5 - Pruebas

        //validar el status code
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("addresses"));
        assertTrue(body_response.contains("alias"));

        //Validar que el nombre de la direccion es el valor que le mande al editarla
        JsonPath jsonResponse = response.jsonPath();
        String direcctionNombre = jsonResponse.get("addresses."+addressID+".contact");
        assertEquals(direcctionNombre, name);

        //Validar que el alias de la direccion es el valor que le mande al editarla
        String aliasDireccion = jsonResponse.get("addresses."+addressID+".alias");
        assertEquals(aliasDireccion, alias);

        //validar que el tiempo de respuesta sea menor a 2s
        long tiempo = response.getTime();
        assertTrue(tiempo < 2000);

        //Validar que los headers contengan application/json
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    @Test
    @Order(15)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("15 Test case: Eliminar direccion")
    public void eliminar_direccion_200(){

        String token = obtener_Token();

        RestAssured.baseURI = String.format("https://%s/addresses/v1/delete/%s",url_base,addressID);
        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .header("Accept","*/*")
                .auth().preemptive().basic(uuid,token)
                .delete();

        String body_response = response.getBody().prettyPrint();
        //Junit 5 - Pruebas

        //validar el status code
        assertEquals(200,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("message"));

        //Validar que el nombre de la direccion es el valor que le mande al crearla
        JsonPath jsonResponse = response.jsonPath();
        String message = jsonResponse.get("message");
        assertEquals(message, ""+addressID+" deleted correctly");

        //validar que el tiempo de respuesta sea menor a 2s
        long tiempo = response.getTime();
        assertTrue(tiempo < 2000);

        //Validar que los headers contengan application/json
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

    //@Test
    @Order(16)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("16 Test case: Ver Direccion despues de editar")
    public void ver_direccion_despues_de_eliminar_400(){

        String token = obtener_Token();

        RestAssured.baseURI = String.format("https://%s/addresses/v1/get/%s",url_base,addressID);
        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .header("Accept","*/*")
                .auth().preemptive().basic(uuid,token)
                .get();

        String body_response = response.getBody().prettyPrint();
        //Junit 5 - Pruebas

        //validar el status code
        assertEquals(400,response.getStatusCode());

        //Validar que el body response no venga vacio
        assertNotNull(body_response);

        //Validar quel body response contenga algunos campos
        assertTrue(body_response.contains("error"));

        //Validar que el nombre de la direccion es el valor que le mande al crearla
        JsonPath jsonResponse = response.jsonPath();
        String errorMessage = jsonResponse.get("error");
        assertEquals(errorMessage, "Error getting the address: sql: no rows in result set");


        long tiempo = response.getTime();
        assertTrue(tiempo < 2000);

        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("application/json"));

    }

}