var cutil = {
    datagrid: {
        getSelections: function (selector, hintMsg) {
            if (!hintMsg) {
                hintMsg = '请选择数据！';
            }
            var selections = $(selector).datagrid('getSelections');
            if (selections.length == 0) {
                $.messager.alert('警告', hintMsg, 'warning');
                return false;
            } else {
                return selections;
            }
        },
        getSelection: function (selector, hintMsg) {
            if (!hintMsg) {
                hintMsg = '请选择一条数据！';
            }
            var selections = $(selector).datagrid('getSelections');
            if (selections.length != 1) {
                $.messager.alert('警告', hintMsg, 'warning');
                return false;
            } else {
                return selections[0];
            }
        },
        getSelectionsIds: function (selector, idField, hintMsg) {
            if (!idField) {
                idField = 'id';
            }
            if (!hintMsg) {
                hintMsg = '请选择数据！';
            }
            var selections = cutil.datagrid.getSelections(selector, hintMsg);
            if (selections) {
                var ids = [];
                for (var i = 0; i < selections.length; i++) {
                    var id = selections[i][idField];
                    if (!id) {
                        throw "没有id属性";
                    }
                    ids.push(id);
                }
                return ids;
            } else {
                return selections;
            }
        },
        getSelectionId: function (selector, idField, hintMsg) {
            if (!idField) {
                idField = 'id';
            }
            if (!hintMsg) {
                hintMsg = '请选择一条数据！';
            }
            var selections = $(selector).datagrid('getSelections');
            if (selections.length != 1) {
                $.messager.alert('警告', hintMsg, 'warning');
                return false;
            }
            var selection = selections[0];
            if (!selection[idField]) {
                throw "没有id属性";
            }
            return selection[idField];
        }
    },
    request: {
        post: function (url, param, config) {
            if (!config) config = {};
            config.method = 'POST';
            cutil.request.ajax(url, param, config);
        },
        delete: function (url, param, config) {
            if (!config) config = {};
            config.method = 'DELETE';
            cutil.request.ajax(url, param, config);
        },
        ajax: function (url, param, config) {
            $.ajax({
                url: url,
                type: config.method,
                dataType: 'json',
                data: JSON.stringify(param),
                headers: {'Content-Type': 'application/json'},
                success: function (response) {
                    if (response.success) {
                        var successFunc;
                        if (config.success) {
                            successFunc = config.success;
                        } else {
                            successFunc = function (data, response) {
                                if (response.messageOnly) {
                                    $.messager.alert('提示', response.message, 'info');
                                }
                                if (config.closeDialog) {
                                    $(config.closeDialog).dialog('close');
                                }
                                if (config.reloadDatagrid) {
                                    $(config.reloadDatagrid).datagrid('reload');
                                }
                            }
                        }
                        successFunc(response.data, response);
                    } else {
                        // helper.js ajaxSetup全局做了拦截输出错误信息
                        var failFunc;
                        if (config.fail) {
                            failFunc = config.fail;
                            failFunc(response.data, response);
                        }
                    }
                }
            });
        }
    }
};