import 'dart:convert';

import 'package:conglomerate_ui/homepage.dart';
import 'package:conglomerate_ui/loginPage.dart';
import 'package:flutter/material.dart';
import 'package:flutter/cupertino.dart';
import 'package:http/http.dart' as http;
import 'package:string_validator/string_validator.dart';

import 'UserProfile.dart';

//Alice:


//comment here
class MyResetEmailCodePage extends StatefulWidget {
  MyResetEmailCodePage({Key key, this.title}) : super (key : key);
  final String title;
  _MyResetEmailCodeState createState() => _MyResetEmailCodeState();
}

class _MyResetEmailCodeState extends State<MyResetEmailCodePage> {

  void validatePin(String username, String pin, String password) async {
    http.Response r = await http.post('http://134.122.21.105:8080/users/' + username + '/update-password',
      headers: {
        "Accept": "application/json",
        "Content-type": "application/json" },
      body: jsonEncode(<String, String>{
        "newPassword" : password,
        "resetPin": pin,
      }),
    );

    if (r.statusCode == 200) {
      Navigator.push(
        context,
        MaterialPageRoute(builder: (context) => MyHomePage()),
      );
    }
    else {
      createAlertDialog(context, "Couldn't update password!");

    }
  }

  TextStyle style = TextStyle(fontFamily: 'Molengo', fontSize: 20.0);
  final username_controller = TextEditingController();
  final pin_controller = TextEditingController();
  final newP_controller = TextEditingController();
  final repeat_controller = TextEditingController();



  @override
  Widget build(BuildContext context) {
    final pageName = Text(
      'Enter code sent to your email',
      style: TextStyle(
          fontFamily: 'Molengo',
          fontSize: 40,
          color: Color(0xff1e6b87)
      ),
      textAlign: TextAlign.center,
    );
    final usernameField = TextField(
      obscureText: false,
      style: style,
      controller: username_controller,
      decoration: InputDecoration(
          contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          hintText: "Username",
          border:
          OutlineInputBorder(borderRadius: BorderRadius.circular(32.0))),
    );

    final pinField = TextField(
      obscureText: false,
      style: style,
      controller: pin_controller,
      decoration: InputDecoration(
          contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          hintText: "Pin",
          border:
          OutlineInputBorder(borderRadius: BorderRadius.circular(32.0))),
    );

    final newPField = TextField(
      obscureText: true,
      style: style,
      controller: newP_controller,
      decoration: InputDecoration(
          contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          hintText: "New Password",
          border:
          OutlineInputBorder(borderRadius: BorderRadius.circular(32.0))),
    );

    final resetField = TextField(
      obscureText: true,
      style: style,
      controller: repeat_controller,
      decoration: InputDecoration(
          contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          hintText: "Repeat Password",
          border:
          OutlineInputBorder(borderRadius: BorderRadius.circular(32.0))),
    );

    // log in button

    final enterButton = MaterialButton(
        minWidth: MediaQuery.of(context).size.width,
        height: 50,

        //padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
        color: Color(0xff7ddcff),

        onPressed: () {


          if (!checkPassword(newP_controller.text)) {
            createAlertDialog(context, "Invalid password");
          }
          else if (newP_controller.text != repeat_controller.text) {
          createAlertDialog(context, "Passwords do not match");
          }
          else {
            validatePin(username_controller.text, pin_controller.text,
                newP_controller.text);
          }

        },
        child: Text("Confirm",
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
          actions: <Widget>[
            IconButton(
              color: Color(0xff1e6b87),
              icon: const Icon(Icons.home),
              onPressed: () {
                Navigator.push(context, MaterialPageRoute(builder: (context)=> MyLoginPage()));
              },
            )
          ]
      ),
      resizeToAvoidBottomPadding: false,
      body: Center(
        child: Container(
          color: Colors.white,
          child: Padding(
            padding: const EdgeInsets.all(36.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[

                pageName,
                SizedBox(height: 15,),
                usernameField,
                SizedBox(height: 15.0,),
                pinField,
                SizedBox(height: 15,),
                newPField,
                SizedBox(height: 15,),
                resetField,
                SizedBox(height: 15,),
                enterButton,
              ],
            ),
          ),
        ),
      ),
    );
  }
}

bool checkPassword(String password) {
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
if (isAlphanumeric(tester) && password.length >= 8 && password.length <= 16) {
return true;
}
return false;

}

