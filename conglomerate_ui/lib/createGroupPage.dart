import 'dart:convert';
import 'dart:io' as Io;
import 'package:image_picker/image_picker.dart';
import 'package:conglomerate_ui/createpage.dart';
import 'package:conglomerate_ui/loginPage.dart';
import 'package:conglomerate_ui/resetEmailCode.dart';
import 'package:conglomerate_ui/userHomePage.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:conglomerate_ui/homepage.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:flutter_barcode_scanner/flutter_barcode_scanner.dart';

//Alice:

//comment here
class CreateGroupPage extends StatefulWidget {
  CreateGroupPage({Key key, this.title}) : super(key: key);
  final String title;

  CreateGroupState createState() => CreateGroupState();
}

createAlertDialog(BuildContext context, String message) {
  return showDialog(
      context: context,
      builder: (context) {
        return CupertinoAlertDialog(
          title: Text(message,
              style:
                  TextStyle(fontFamily: "Molengo", color: Color(0xff535a70))),
          actions: <Widget>[
            MaterialButton(
                child: Text("OK"),
                onPressed: () {
                  Navigator.of(context).pop();
                })
          ],
        );
      });
}

class CreateGroupState extends State<CreateGroupPage> {
  TextStyle style = TextStyle(fontFamily: 'Molengo', fontSize: 20.0);
  final grp_name_controller = TextEditingController();
  final grp_code_controller = TextEditingController();
  Io.File imageFile;
  String _scanBarcode = "";

  joinGrpWithCode(String groupid) async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String authT = prefs.getString('authToken');
    http.Response r =
        await http.post('http://134.122.21.105:8080/groupings/join',
            headers: {
              "Accept": "application/json",
              "Content-type": "application/json",
              "authorization": "bearer " + authT
            },
            body: groupid);
    if (r.statusCode == 200) {
      Navigator.push(
        context,
        MaterialPageRoute(builder: (context) => userHomePage()),
      );
      createAlertDialog(context, "Group joined successfully!");
    } else {
      createAlertDialog(context, "Couldn't join group!");
    }
  }

  getGrpCodeDialog(BuildContext context) {
    TextStyle alertstyle =
        new TextStyle(fontFamily: "Molengo", color: Color(0xff535a70));

    TextField grpCodeField = TextField(
        obscureText: false,
        style: style,
        controller: grp_code_controller,
        decoration: InputDecoration(hintText: "Enter Group Code"));

    return showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog(
            content: Card(
                color: Colors.transparent,
                elevation: 0.0,
                child: Column(
                  children: <Widget>[grpCodeField],
                )),
            actions: <Widget>[
              MaterialButton(
                child: Text("Join", style: alertstyle),
                onPressed: () {
                  Navigator.pop(context);
                  joinGrpWithCode(grp_code_controller.text);
                },
              )
            ],
          );
        });
  }

  openCamera() async {
    var picture = await ImagePicker.pickImage(source: ImageSource.camera);
    //TODO: validate QR code and add the grp to the user.
    this.setState(() {
      imageFile = picture;
    });
  }

  void scanBarCode() async {
    String barcodeScanRes;

    barcodeScanRes = await FlutterBarcodeScanner.scanBarcode(
        "#ff6666", "Cancel", true, ScanMode.QR);
    print(barcodeScanRes);
    joinGrpWithCode(barcodeScanRes);

    setState(() {
      _scanBarcode = barcodeScanRes;
    });
  }

  createJoinOptDialog(BuildContext context) {
    TextStyle alertstyle =
        new TextStyle(fontFamily: "Molengo", color: Color(0xff535a70));
    return showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog(
            title: Text(
              "Join Group using:",
              style: alertstyle,
            ),
            actions: <Widget>[
              MaterialButton(
                  child: Text("Group Code", style: alertstyle),
                  onPressed: () {
                    Navigator.pop(context);
                    getGrpCodeDialog(context);
                  }),
              MaterialButton(
                child: Text("QR code", style: alertstyle),
                onPressed: () {
                  Navigator.pop(context);
                  scanBarCode();
                  //openCamera();
                },
              )
            ],
          );
        });
  }

  @override
  Widget build(BuildContext context) {
    final bottom = MediaQuery.of(context).viewInsets.bottom;

    final pageName = Text(
      'CREATE GROUP',
      style: TextStyle(
          fontFamily: 'Molengo', fontSize: 42, color: Color(0xff1e6b87)),
      textAlign: TextAlign.center,
    );

    final grpnameField = TextField(
      obscureText: false,
      style: style,
      controller: grp_name_controller,
      decoration: InputDecoration(
          contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
          hintText: "Group Name",
          border:
              OutlineInputBorder(borderRadius: BorderRadius.circular(32.0))),
    );

    final createGrpButton = MaterialButton(
        minWidth: MediaQuery.of(context).size.width,
        //padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
        color: Color(0xff7ddcff),
        onPressed: () async {
          String group = grp_name_controller.text;
          SharedPreferences prefs = await SharedPreferences.getInstance();
          String authT = prefs.getString('authToken');
          http.Response r =
              await http.post('http://134.122.21.105:8080/groupings',
                  headers: {
                    "Accept": "application/json",
                    "Content-type": "application/json",
                    "authorization": "bearer " + authT
                  },
                  body: group);
          if (r.statusCode == 201) {
            //TODO: change to pop
//            Navigator.push(
//              context,
//              MaterialPageRoute(
//                  builder: (context) => userHomePage(label: group)),
//            );
          Navigator.pop(context);
            createAlertDialog(context, "Group created successfully!");
          } else {
            createAlertDialog(context, "Couldn't add group!");
          }
        },
        child: Text(
          "Create",
          textAlign: TextAlign.center,
          style: style.copyWith(fontFamily: 'Molengo'), //,
          //fontWeight: FontWeight.bold),
        ),
        textColor: Color(0xff535a70),
        elevation: 2,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10),
        ));

    final joinGrpButton = MaterialButton(
        minWidth: MediaQuery.of(context).size.width,
        color: Color(0xff7ddcff),
        onPressed: () {
          //giving options to join the grp
          createJoinOptDialog(context);
        },
        child: Text(
          "Join",
          textAlign: TextAlign.center,
          style: style.copyWith(fontFamily: 'Molengo'), //,
          //fontWeight: FontWeight.bold),
        ),
        textColor: Color(0xff535a70),
        elevation: 2,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10),
        ));

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
        child: Padding(
          padding: EdgeInsets.only(bottom: bottom, top: 30.0, right: 30.0, left: 30.0),
          child: Container(
            color: Colors.white,
            child: Padding(
              padding: const EdgeInsets.all(30.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  pageName,
                  SizedBox(height: 25),
                  grpnameField,
                  SizedBox(height: 30),
                  createGrpButton,
                  SizedBox(height: 10),
                  joinGrpButton
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
