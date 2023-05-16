package api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Assert;
import org.junit.Test;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class RegresTest {
    private final static String URL = "https://reqres.in/";

@Test
    public void checkAvatarAndIdTest(){
    List<UserData> users = given()
            .when()
            .contentType(ContentType.JSON)
            .get(URL + "api/users?page=2")
            .then().log().all()
            .extract().body().jsonPath().getList("data", UserData.class);
            users.forEach(x-> Assert.assertTrue(x.getAvatar().contains(x.getId().toString())));
            Assert.assertTrue(users.stream().allMatch(x->x.getEmail().endsWith("@reqres.in")));

            List<String> avatars = users.stream().map(UserData :: getAvatar).collect(Collectors.toList());
            List<String> ids = users.stream().map(x->x.getId().toString()).collect(Collectors.toList());
            for(int y = 0; y<avatars.size(); y++){
                Assert.assertTrue(avatars.get(y).contains(ids.get(y)));
            }
    }

    @Test
    public void successRegTest() {
   Integer id = 4;
   String token = "QpwL5tke4Pnpja7X4";
   Register user = new Register("eve.holt@reqres.in", "pistol");
   SuccessReg successReg = given()
        .body(user)
        .when()
        .contentType(ContentType.JSON)
        .post(URL + "api/register")
        .then().log().all()
        .extract().as(SuccessReg.class);
   Assert.assertNotNull(successReg.getId());
   Assert.assertNotNull(successReg.getToken());
assertEquals(id,successReg.getId());
assertEquals(token,successReg.getToken());
    }

    @Test
    public void unsuccessRegTest(){
    Register user = new Register("sydney@fife", "");
    UnSuccess unSuccess = given()
            .body(user)
            .post(URL + "api/register")
            .then().log().all()
            .statusCode(400)
            .extract().as(UnSuccess.class);
    assertEquals("Missing email or username",unSuccess.getError());
    }

    @Test
    public void sortedYearsTest (){
    List<ColorsData> colors = given()
            .when()
            .get(URL + "api/unknown")
            .then().log().all()
            .extract().body().jsonPath().getList("data",ColorsData.class);
    List<Integer> years = colors.stream().map(ColorsData::getYear).collect(Collectors.toList());
    List<Integer> sortedYears = years.stream().sorted().collect(Collectors.toList());
    assertEquals(sortedYears,years);
    System.out.println(sortedYears);
    System.out.println(years);
    }

    @Test
    public  void deleteUserTest(){
        RestAssured
                .given()
                .when()
                .delete(URL + "api/uswers/2")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    public void timeTest(){
    UserTime user = new UserTime("morpheus","zion resident");
    UserTimeResponse response = given()
                        .body(user)
            .when()
            .put(URL + "api/users/2")
            .then()
            .log().all()
            .statusCode(200)
            .extract().as(UserTimeResponse.class);
        String regex = "(.{14})$";
        String regex2 = "(.{8})$";//почему непонятно regex  в 2 х случаях удаляет разное количество символов ?
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex,"");
        Assert.assertEquals(currentTime,response.getUpdatedAt().replaceAll(regex2,""));

        System.out.println(currentTime);
        System.out.println(response.getUpdatedAt().replaceAll(regex2,""));
    }
}
