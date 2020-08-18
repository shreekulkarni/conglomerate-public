class fileModel {
  String documentLink;
  String fileName;
  int id;
  int groupingId;
  String uploader;
  String uploadDate;
  bool shared;

  fileModel({this.documentLink,this.fileName, this.id, this.groupingId, this.uploader, this.uploadDate, this.shared});

  factory fileModel.fromJson (Map<String, dynamic> json) {
    String link = json['documentLink'];
    return new fileModel(
      documentLink: link,
      id: json['id'],
      uploadDate: json['uploadDate'],
      uploader: json['uploaderUsername'],
      fileName: link.split('/').last,
      shared: json['shared'],
    );
  }
  factory fileModel.fromFolder (Map<String, dynamic> json) {
    return new fileModel(
        documentLink: json['documentLink'],
        id: json['id'],
        groupingId: json['groupingId'],
        uploadDate: json['uploadDate'],
        uploader: json['uploaderUsername'],
        fileName: json['name'],
        shared: json['shared']
    );

  }

}