import 'package:flutter/material.dart';
import 'package:conglomerate_ui/group.dart';


import 'package:flutter_share/flutter_share.dart';
import 'package:documents_picker/documents_picker.dart';

class documentUploadPage extends StatefulWidget {
  @override
  final group groupObj;
  documentUploadPage({Key key, @required this.groupObj}): super(key: key);
  _documentUploadPageState createState() => _documentUploadPageState();
}

class _documentUploadPageState extends State<documentUploadPage> {
  bool _share = false;
  String getName() {
    return widget.groupObj.name;
  }
  Future<void> shareFile() async {
    List<dynamic> docs = await DocumentsPicker.pickDocuments;
    if (docs == null || docs.isEmpty) return null;

    await FlutterShare.shareFile(
      title: 'Example share',
      text: 'Example share text',
      filePath: docs[0] as String,
    );
  }

  @override
  Widget build(BuildContext context) {
    final uploadButton = MaterialButton(
        minWidth: MediaQuery.of(context).size.width,
        height: 50,

        //padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
        color: Color(0xff7ddcff),

        onPressed: () {
         shareFile();
        },
        child: Text("Log in",
          textAlign: TextAlign.center,
          style: TextStyle(color: Color(0xff535a70), fontSize: 25.0),

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
      body: Center(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(getName(), style: TextStyle(color: Color(0xff1e6b87), fontSize: 40.0),),
            SizedBox(height: 100,),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                Checkbox(
                  activeColor: Color(0xff1e6b87),
                  checkColor: Color(0xff1e6b87),
                  value: _share,
                  onChanged: (bool value) {
                    setState(() {
                      _share = value;
                      print(_share.toString() + "\n");

                    });
                  },
                ),
                Text("Share this document with other group members?", style: TextStyle(color: Color(0xff535a70), fontSize: 16.0),),

              ],
            ),
            SizedBox(height: 80,),
            uploadButton,
          ],
        ),


      ),

    );
  }
}