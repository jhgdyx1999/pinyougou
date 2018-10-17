var customEditor;
KindEditor.ready(function(K) {
    customEditor = K.create('textarea[name="introductionContent"]', {
        resizeType : 1,
        allowPreviewEmoticons : false,
        allowImageUpload : false,
        width : 1550,
        readonlyMode : true,
        items : []
    });
});