

import 'fileModel.dart';

class folderModel {
 List<fileModel> documents;
 int id;
 String folderName;

  folderModel({this.documents, this.id, this.folderName});

  factory folderModel.fromJson (Map<String, dynamic> json) {
    var docs = json['documents'] as List;
    List<fileModel> temp = new List();

    for (var d in docs) {
      temp.add(fileModel.fromFolder(d));
    }

    return new folderModel(
        documents: temp,
        id: json['id'],
      folderName: json['name']

    );
  }

}