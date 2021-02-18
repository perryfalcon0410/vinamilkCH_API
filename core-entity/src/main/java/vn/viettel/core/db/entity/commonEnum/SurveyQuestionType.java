package vn.viettel.core.db.entity.commonEnum;

public enum SurveyQuestionType {
    TEXT(1L,"テキスト回答"),
    CHECKBOX(2L,"複数選択(チェックボックス )"),
    RADIO(3L, "単一選択(ラジオボタン )"),
    DROPDOWN(4L, "単一選択(ドロップダウン )");


    private Long id;
    private String typeName;

    SurveyQuestionType(Long id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public static String getNameById(Long id) {
        for (SurveyQuestionType e : SurveyQuestionType.values()) {
            if (id == e.getId()) {
                return e.getTypeName();
            }
        }
        return null;
    }
}
