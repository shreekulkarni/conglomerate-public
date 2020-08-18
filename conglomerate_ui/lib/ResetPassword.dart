import 'dart:convert';

import 'package:conglomerate_ui/resetEmailCode.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:conglomerate_ui/homepage.dart';
import 'package:http/http.dart' as http;


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

//comment here
class MyResetPage extends StatefulWidget {
  MyResetPage({Key key, this.title}) : super (key : key);
  final String title;
  _MyResetState createState() => _MyResetState();
}

class _MyResetState extends State<MyResetPage> {

  void sendEmail(String username, String email) async {
    http.Response r = await http.post('http://134.122.21.105:8080/users/' + username + '/forgot-password',
      headers: {
        "Accept": "application/json",
        "Content-type": "application/json" },
      body: email,
    );

    if (r.statusCode == 200) {
    Navigator.push(
    context,
    MaterialPageRoute(builder: (context) => MyResetEmailCodePage()),
    );
    }
    else {
    createAlertDialog(context, "Could not send email!");

    }
  }
  TextStyle style = TextStyle(fontFamily: 'Molengo', fontSize: 20.0);
  final username_controller = TextEditingController();
  final email_controller = TextEditingController();


  @override
  Widget build(BuildContext context) {
    final pageName = Text(
      'RESET PASSWORD',
      style: TextStyle(
          fontFamily: 'Molengo',
          fontSize: 40,
          color: Color(0xff1e6b87)
      ),
      textAlign: TextAlign.center,
    );

    final emailField = TextField(
      obscureText: false,
      style: style,
      controller: email_controller,
      decoration: InputDecoration(
          contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          hintText: "Email",
          border:
          OutlineInputBorder(borderRadius: BorderRadius.circular(32.0))),
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


    // log in button

    final sendEmailButton = MaterialButton(
        minWidth: MediaQuery.of(context).size.width,
        height: 50,

        //padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
        color: Color(0xff7ddcff),

        onPressed: () {

          sendEmail(username_controller.text, email_controller.text);

        },
        child: Text("Send reset code",
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
                Navigator.push(context, MaterialPageRoute(builder: (context)=> MyHomePage()));
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
                SizedBox(
                  child: Image.asset(
                      "assets/logo.png",
                      fit: BoxFit.fill,
                      scale:0.1
                  ),
                ),
                pageName,
                SizedBox(height: 30,),
                usernameField,
                SizedBox(height: 15),
                emailField,
                SizedBox(height: 15.0,),
                sendEmailButton,
              ],
            ),
          ),
        ),
      ),
    );
  }
}
