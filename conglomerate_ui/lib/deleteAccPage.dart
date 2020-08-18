import 'package:conglomerate_ui/homepage.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class DeleteAcc extends StatefulWidget {
  DeleteAcc({Key key, this.title}) : super (key : key);
  final String title;
  _DeleteAccState createState() => _DeleteAccState();
}

class _DeleteAccState extends State<DeleteAcc> {
  TextStyle style = TextStyle(fontFamily: 'Molengo', fontSize: 25.0);

  createConfirmDialog(BuildContext context) {
    TextStyle alertstyle = new TextStyle(fontFamily: "Molengo", color: Color(0xff535a70));
    return showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog (
            title: Text("Click OK to confirm the deletion of your account", style: alertstyle),
            actions: <Widget>[
              MaterialButton(
                  child: Text("OK", style: alertstyle),
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (context) => MyHomePage()),
                    );
                  }//TODO: implement deletion
              ),
              MaterialButton(
                child: Text("Cancel", style: alertstyle),
                onPressed: () {
                  Navigator.of(context).pop();
                },
              )
            ],
          );
        });
  }

  @override
  Widget build(BuildContext context) {
    final pageName = Text(
      'Delete Account',
      style: TextStyle(
          fontFamily: 'Molengo',
          fontSize: 30,
          color: Color(0xff1e6b87)
      ),
      textAlign: TextAlign.center,
    );

    final createDeleteButton = MaterialButton(
        minWidth: MediaQuery.of(context).size.width,
        height: 50,

        //padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
        color: Color(0xff4fafd1),
        child: Text("Confirm Deletion",
          textAlign: TextAlign.center,
          style: style.copyWith(
              fontFamily: 'Molengo'),//,
          //fontWeight: FontWeight.bold),
        ),
        onPressed: () {
          createConfirmDialog(context);
        },
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
                SizedBox(height: 60),
                pageName,
                SizedBox(height: 130,),
                createDeleteButton,
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

class confirmDeletion extends StatefulWidget {
  State<StatefulWidget> createState() => confirmDeletionState();
}

class confirmDeletionState extends State<confirmDeletion> {
  @override
  Widget build(BuildContext context) {

  }
}
