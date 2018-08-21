class Utils extends Object {
  static int baseId = 100;
  static String pageNameSeperatorToken = "_";

  static int generatePrimaryPageId() {
    return baseId++;
  }

  static Map parseUniquePageName(String pageName) {
    List components = pageName.split(pageNameSeperatorToken);
    if (components.length != 2) return null;
    return {"name": components[0], "id": components[1]};
  }

  static String generateUniquePageName(String pageName) {
    return (pageName ?? "") +
        pageNameSeperatorToken +
        generatePrimaryPageId().toString();
  }
}
