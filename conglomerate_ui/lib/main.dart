import 'package:conglomerate_ui/homepage.dart';
import 'package:flutter/material.dart';

import 'messagingPage.dart';


void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: "Create Account Page",
      theme: ThemeData(
        primaryColor: Colors.blue,
      ),
      //home: messagingPage(groupName: "grp name", chatID: "1",),
      home: MyHomePage()
    );
  }
}



