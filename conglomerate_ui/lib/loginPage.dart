import 'package:conglomerate_ui/ResetPassword.dart';
import 'package:conglomerate_ui/homepage.dart';
import 'package:conglomerate_ui/userHomePage.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:async';
import 'dart:convert';

//Alice:


createAlertDialog(BuildContext context, String message) {
  return showDialog(
      context: context,
      builder: (context) {
        return CupertinoAlertDialog (
          title: Text(message,
              style: TextStyle(fontFamily: "Molengo", color: Color(0xff535a70))),
          actions: <Widget>[
            MaterialButton(
                child: Text("OK"),
                onPressed: () {
                  Navigator.of(context).pop();
                }
            )
          ],
        );
      });
}

class User {
  final String username;
  final String password;

  User({this.username, this.password});

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      username: json['username'],
      password: json['password'],
    );
  }
}


//comment here
class MyLoginPage extends StatefulWidget {


  MyLoginPage({Key key, this.title}) : super (key : key);
  final String title;
  _MyLoginState createState() => _MyLoginState();
}

class _MyLoginState extends State<MyLoginPage> {
  void validateCredentials(String username, String password) async {
    http.Response r;
    try {
      r = await http.post('http://134.122.21.105:8080/users/login',
        headers: {
          "Accept": "application/json",
          "Content-type": "application/json"},
        body: jsonEncode(<String, String>{
          "password": password,
          "username": username
        }),
      );
    }
    catch (e) {
      createAlertDialog(context, "Could not connect to server, try again later!");
    }
    if (r.statusCode == 200) {
      SharedPreferences prefs = await SharedPreferences.getInstance();
      await prefs.setString('authToken', r.body);
      await prefs.setString('username', username);
      prefs.setString("cur_user", username);
      http.Response r2 = await http.post('http://134.122.21.105:8080/users/get-profile-picture',
        headers: {
          "Accept": "application/json",
          "Content-type": "application/json" },
        body: jsonEncode(<String, String>{
          "authToken" : r.body,
        }),
      );
      if (r2.statusCode == 200) {
        prefs.setString('pic', r2.body);
      }
      Navigator.push(
        context,
        MaterialPageRoute(builder: (context) => userHomePage()),
      );
    }
    else if (r.statusCode == 400){
      createAlertDialog(context, "The Username or Password is incorrect please enter again!");
    }
    else {
      createAlertDialog(context, "Couldn't log in! Try loggin in again or contacting customer support for help!");
    }
  }

  TextStyle style = TextStyle(fontFamily: 'Molengo', fontSize: 20.0);
  final username_controller = TextEditingController();
  final password_controller = TextEditingController();

  @override
  Widget build(BuildContext context) {
    final bottom = MediaQuery.of(context).viewInsets.bottom;

    final pageName = Text(
      'LOGIN',
      style: TextStyle(
          fontFamily: 'Molengo',
          fontSize: 40,
          color: Color(0xff1e6b87)
      ),
      textAlign: TextAlign.center,
    );
    final usernameField = TextFormField(
//      focusNode: _focusNode,
      obscureText: false,
      style: style,
      controller: username_controller,
      decoration: InputDecoration(
          contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          hintText: "Username",
          border:
          OutlineInputBorder(borderRadius: BorderRadius.circular(32.0))),
    );

    final passwordField = TextFormField(
      obscureText: true,
      style: style,
      controller: password_controller,
      decoration: InputDecoration(
          contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          hintText: "Password",
          border:
          OutlineInputBorder(borderRadius: BorderRadius.circular(32.0))),
    );


    // log in button

    final logInButton = MaterialButton(
        minWidth: MediaQuery.of(context).size.width,
        height: MediaQuery.of(context).size.height / 18,

        //padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
        color: Color(0xff7ddcff),

        onPressed: () {
          String username = username_controller.text;
          String password = password_controller.text;

          try {
            validateCredentials(username, password);
          }
          catch (e) {
            createAlertDialog(context, "Could not connect to server, try again later!");
            return;
          }

        },
        child: Text("Log in",
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

    final resetPasswordButton = MaterialButton(
        minWidth: MediaQuery.of(context).size.width,
        height: MediaQuery.of(context).size.height / 18,

        //padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
        color: Color(0xff7ddcff),

        onPressed: () {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => MyResetPage()),
          );

        },
        child: Text("Forgot password?",
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
      resizeToAvoidBottomInset: false,
      resizeToAvoidBottomPadding: false,
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
      body: SingleChildScrollView(
        reverse: true,
        child: Container(
//          color: Colors.white,
          child: Padding(
            padding: EdgeInsets.only(bottom: bottom, top: 30.0, right: 30.0, left: 30.0),
              child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                SizedBox(

                  child: Image.asset(
                    "assets/logo.png",
                    //fit: BoxFit.fill,
                    scale:0.1
                  ),
                ),
                pageName,
                SizedBox(height: 30,),
                usernameField,
                SizedBox(height: 15.0,),
                passwordField,
                SizedBox(height: 20.0,),
                logInButton,
                SizedBox(height: 10,),
                resetPasswordButton,
                SizedBox(height: 10,)
              ],
            )
          )
          ),
        ),
      );
  }
}

