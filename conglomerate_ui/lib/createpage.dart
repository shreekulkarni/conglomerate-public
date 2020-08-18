
import 'dart:convert';

import 'package:conglomerate_ui/loginPage.dart';
import 'package:conglomerate_ui/userHomePage.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:string_validator/string_validator.dart';
import 'package:http/http.dart' as http;
//Vanshika:
class Validate {
  static bool checkEmail(String email) {
    if (isEmail(email)) {
      return true;
    }
    return false;
  }
  static bool checkUsername(String username) {
    //check database
    String tester = username.replaceAll("_", "a");
    if (isAlphanumeric(tester)) {
      return true;
    }
    return false;

  }
  //Vanshika: comment
  static bool checkPassword(String password) {
    String tester = password.replaceAll("_", "a");
    tester = tester.replaceAll("!", "a");
    tester = tester.replaceAll("@", "a");
    tester = tester.replaceAll("#", "a");
    tester = tester.replaceAll(r"$", "a");
    tester = tester.replaceAll("%", "a");
    tester = tester.replaceAll("^", "a");
    tester = tester.replaceAll("&", "a");
    tester = tester.replaceAll("*", "a");
    tester = tester.replaceAll(")", "a");
    tester = tester.replaceAll("(", "a");
    tester = tester.replaceAll("_", "a");
    tester = tester.replaceAll("-", "a");
    tester = tester.replaceAll("+", "a");
    tester = tester.replaceAll("=", "a");
    tester = tester.replaceAll(".", "a");
    tester = tester.replaceAll(",", "a");
    if (isAlphanumeric(tester)) {
      return true;
    }
    return false;

  }


}


//comment here
class MyCreatePage extends StatefulWidget {
  MyCreatePage({Key key, this.title}) : super (key : key);
  final String title;
  _MyCreatePageState createState() => _MyCreatePageState();
}

class _MyCreatePageState extends State<MyCreatePage> {

  void getValid(String url, String url2, String username, String email, String password) async{
    var response;
    var response2;

    //Check if username/email already exists

    try {
      response = await http.get(url);
      response2 = await http.get(url2);
    } catch (e) {
      createAlertDialog(context, "Could not connect to server, try again later!");
      return;
    }

    //Valid details

    if (response.body.contains("false") && response2.body.contains("false")) {
      http.Response r = await http.post('http://134.122.21.105:8080/users',
        headers: {
        "Accept": "application/json",
        "Content-type": "application/json" },
        body: jsonEncode(<String, String>{
          "email": email,
          "passwordHash": password,
          "userName": username
        }),
      );
      if (r.statusCode == 201) {

        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => MyLoginPage()),
        );
        createAlertDialog(context, "Account created successfully");
      }
      else {
        createAlertDialog(context, "Couldn't add user!");

      }
    }

    if (response.body.contains("true")) {
      createAlertDialog(context, "Username already exists!");
    }
    if (response2.body.contains("true")) {
      createAlertDialog(context, "Email already exists! Try logging in instead.");
    }

    return;
  }

  TextStyle style = TextStyle(fontFamily: 'Molengo', fontSize: 20.0);
  final usernameController = TextEditingController();
  final emailController = TextEditingController();
  final passwordController = TextEditingController();
  final passwordReenterController = TextEditingController();


  createAlertDialog(BuildContext context, String message) {
    TextStyle alertstyle = new TextStyle(fontFamily: "Molengo", color: Color(0xff535a70));
    return showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog (
            title: Text(message,
                style: alertstyle),
            actions: <Widget>[
              MaterialButton(
                  child: Text("OK", style: alertstyle),
                  onPressed: () {
                    Navigator.of(context).pop();
                  }
              )
            ],
          );
        });
  }


  @override
  Widget build(BuildContext context) {
    bool clicked = false;
    final bottom = MediaQuery.of(context).viewInsets.bottom;

    final pageName = Text(
      'CREATE ACCOUNT',
      style: TextStyle(
          fontFamily: 'Molengo',
          fontSize: 42,
          color: Color(0xff1e6b87)
      ),
      textAlign: TextAlign.center,

    );

    final usernameFiled = TextField(
      autofocus: true,
      obscureText: false,
      style: style,
      controller: usernameController,
      decoration: InputDecoration(
          contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          hintText: "Username: 5-20 letters or '_'",
          border:
          OutlineInputBorder(borderRadius: BorderRadius.circular(32.0))),
    );

    final emailField = TextField(
      autofocus: true,
      obscureText: false,
      style: style,
      controller: emailController,
      decoration: InputDecoration(
          contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          hintText: "Email",
          border:
          OutlineInputBorder(borderRadius: BorderRadius.circular(32.0))),
    );

    final passwordField = TextField(
      autofocus: true,
      obscureText: true,
      style: style,
      controller: passwordController,
      decoration: InputDecoration(
          contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          hintText: "Password: 8-16 letters or symbols",
          border:
          OutlineInputBorder(borderRadius: BorderRadius.circular(32.0))),
    );
    final passwordReenterField = TextField(
      autofocus: true,
      obscureText: true,
      style: style,
      controller: passwordReenterController,
      decoration: InputDecoration(
          contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          hintText: "Re-enter Password",
          border:
          OutlineInputBorder(borderRadius: BorderRadius.circular(32.0))),
    );

    final SnackBar accCreatedSnackbar = new SnackBar(
      content: Text("Account Created!", style: TextStyle(color: Colors.white)),
      backgroundColor: Color(0xff1e6b87).withOpacity(0.5),
    );

    final createButton = MaterialButton(
      minWidth: MediaQuery.of(context).size.width,
        height: MediaQuery.of(context).size.height / 18,
        //padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
        color: Color(0xff7ddcff),
        onPressed: () {

          bool validUser = false;
          bool validEmail = false;
          bool validPass = false;

          String message = "INVALID FIELDS:\n";

          String username = usernameController.text;
          String email = emailController.text;
          String password = passwordController.text;
          setState(() {
            clicked == false
                ? clicked = true
                : clicked = false;

          });
          if (Validate.checkUsername(username)) {

            validUser = true;
          } else {
            message += "Username: Invalid Characters\n";
          }
          if (username.length < 5 || username.length > 20) {
            message += "Username: Wrong length\n";
            validUser = false;
          }

          if (Validate.checkEmail(email)) {
            validEmail = true;
          } else {
            message += "Email: Invalid Email\n";
          }
          if (Validate.checkPassword(password)) {
            validPass = true;
          } else {
            message += "Password: Invalid Characters\n";
          }
          if (password.length > 16 || password.length < 8) {
            message += "Password: Wrong length\n";
            validPass = false;
          }

          if (passwordController.text != passwordReenterController.text) {
            validPass = false;
            message += "Passwords do not match";
          }


          if (!validUser || !validEmail || !validPass) {
            createAlertDialog(context, message);
          } else {
            getValid('http://134.122.21.105:8080/users/username/' + usernameController.text + '/exists','http://134.122.21.105:8080/users/email/' + emailController.text + '/exists', username, email, password);

          }
        },

        child: Text("Create Account",
            textAlign: TextAlign.center,
            style: style.copyWith(
                fontFamily: 'Molengo'),//,
                //fontWeight: FontWeight.bold),
        ),
      textColor: Color(0xff535a70),
      elevation: 2,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(10),
      )
    );
    


    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.arrow_back_ios),
          color: Color(0xff1e6b87),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
      ),
      resizeToAvoidBottomPadding: false,
      resizeToAvoidBottomInset: false,
      body: SingleChildScrollView(
        reverse: true,
        child: Container(
//          color: Colors.white,
          child: Padding(
            padding: EdgeInsets.only(left: 30, top:30, right: 30, bottom: bottom),

            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[

                pageName,
                SizedBox(

                  child: Image.asset("assets/logo.png",
                      fit: BoxFit.contain,
                  ),
                ),
                usernameFiled,
                SizedBox(height: 5.0),
                emailField,
                SizedBox(height: 5.0),
                passwordField,

                SizedBox(
                  height: 5.0,
                ),
                passwordReenterField,
                SizedBox(
                  height: 25.0,
                ),
                createButton,
                SizedBox(
                  height: 15.0,
                ),
              ],
            ),
          ),
        ),
//        ],
      ),
    );
  }
}

class errorDialog extends StatefulWidget {
  State<StatefulWidget> createState() => errorDialogState();
}

class errorDialogState extends State<errorDialog> with SingleTickerProviderStateMixin {
  AnimationController controller;
  Animation<double> scaleAnimation;

  void initState() {
    super.initState();

    controller =
        AnimationController(vsync: this, duration: Duration(milliseconds: 450));
    scaleAnimation =
        CurvedAnimation(parent: controller, curve: Curves.elasticInOut);

    controller.addListener(() {
      setState(() {});
    });

    controller.forward();
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Material(
        color: Colors.transparent,
        child: ScaleTransition(
          scale: scaleAnimation,
          child: Container(
            decoration: ShapeDecoration(
                color: Colors.white,
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(15.0))),
            child: Padding(
              padding: const EdgeInsets.all(50.0),
              child: Text("Invalid Password"),
            ),
          ),
        ),
      ),
    );
  }
}
