import 'package:conglomerate_ui/createpage.dart';
import 'package:conglomerate_ui/groupHomePage.dart';
import 'package:conglomerate_ui/loginPage.dart';
import 'package:conglomerate_ui/userHomePage.dart';
import 'package:flutter/material.dart';

//Alice:

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super (key : key);
  final String title;
  _MyHomePageState createState() => _MyHomePageState();
}



class _MyHomePageState extends State<MyHomePage> {
  TextStyle style = TextStyle(fontFamily: 'Molengo', fontSize: 20.0);

  @override
  Widget build(BuildContext context) {
    final pageName = Text(
      'CONGLOMERATE',
      style: TextStyle(
          fontFamily: 'Molengo',
          fontSize: MediaQuery.of(context).size.height/20,
          color: Color(0xff1e6b87)
      ),
      textAlign: TextAlign.center,
    );

    final createAccountButton = MaterialButton(
        minWidth: MediaQuery.of(context).size.width,
        height: 50,

        //padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
        color: Color(0xff7ddcff),

        onPressed: () {

          Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => MyCreatePage()),
          );

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

    // log in button

    final logInButton = MaterialButton(
        minWidth: MediaQuery.of(context).size.width,
        height: 50,

        //padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
        color: Color(0xff7ddcff),

        onPressed: () {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => MyLoginPage()),
          );

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


    return Scaffold(
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
                     fit: BoxFit.contain,
                      scale: 0.01,
                 ),
                ),
                pageName,
                SizedBox(height: 50,),
                createAccountButton,
                SizedBox(height: 50,),
                logInButton,
                SizedBox(
                  height: 15.0,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
