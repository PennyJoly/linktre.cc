/**
 * 查找当前这个节点的所有节点(包含子节点)，并进行折叠或者展开操作
 *
 * @param item 被点击条目的子一级条目
 * @param target 整个bootstrap tree table实例
 * @param globalCollapsedFlag 如果为true，则表示当前操作是收缩（折叠），如果是false，表示当前操作是展开
 * @param options 存放了一些常量，例如展开和收缩的class
 */
function extracted($, item, target, globalCollapsedFlag, options) {
    var itemCodeName = $(item).find("td[name='code']").text();
    var subItems = target.find("tbody").find(".tg-" + itemCodeName);//下一级，改为下所有级别

    if (subItems.size() > 0) {
        $.each(subItems, function (nIndex, nItem) {
            extracted($, nItem, target, globalCollapsedFlag, options);
        });
    }
    $.each(subItems, function (pIndex, pItem) {

        //如果是展开,判断当前箭头是开启还是关闭
        var expander = $(item).find("td[name='name']").find(".treetable-expander");
        if (!globalCollapsedFlag) {
            var hasExpander = expander.hasClass(options.expanderExpandedClass);
            if (hasExpander) {
                $(pItem).css("display", "table");
            } else {
                $(pItem).css("display", "none");
            }
        } else {
            //如果是折叠，就把当前开着的都折叠掉
            $(pItem).css("display", "none");
            expander.removeClass(options.expanderExpandedClass);
            expander.addClass(options.expanderCollapsedClass);
        }
    });
}

(function ($) {
    "use strict";

    $.fn.bootstrapTreeTable = function (options, param) {
        var allData = null;//用于存放格式化后的数据
        // 如果是调用方法
        if (typeof options == 'string') {
            return $.fn.bootstrapTreeTable.methods[options](this, param);
        }
        // 如果是初始化组件
        options = $.extend({}, $.fn.bootstrapTreeTable.defaults, options || {});
        // 是否有radio或checkbox
        var hasSelectItem = false;
        var target = $(this);
        // 在外层包装一下div，样式用的bootstrap-table的
        var _main_div = $("<div class='bootstrap-tree-table fixed-table-container'></div>");
        target.before(_main_div);
        _main_div.append(target);
        target.addClass("table table-hover treetable-table table-bordered");
        if (options.striped) {
            target.addClass('table-striped');
        }
        // 工具条在外层包装一下div，样式用的bootstrap-table的
        if (options.toolbar) {
            var _tool_div = $("<div class='fixed-table-toolbar'></div>");
            var _tool_left_div = $("<div class='bs-bars pull-left'></div>");
            _tool_left_div.append($(options.toolbar));
            _tool_div.append(_tool_left_div);
            _main_div.before(_tool_div);
        }
        // 格式化数据，优化性能
        target.formatData = function (data) {
            var _root = options.rootCodeValue ? options.rootCodeValue : null
            $.each(data, function (index, item) {
                // 添加一个默认属性，用来判断当前节点有没有被显示
                item.isShow = false;
                // 这里兼容几种常见Root节点写法
                // 默认的几种判断
                var _defaultRootFlag = item[options.parentCode] == '0'
                    || item[options.parentCode] == 0
                    || item[options.parentCode] == null
                    || item[options.parentCode] == '';
                if (!item[options.parentCode] || (_root ? (item[options.parentCode] == options.rootCodeValue) : _defaultRootFlag)) {
                    if (!allData["_root_"]) {
                        allData["_root_"] = [];
                    }
                    allData["_root_"].push(item);
                } else {
                    if (!allData["_n_" + item[options.parentCode]]) {
                        allData["_n_" + item[options.parentCode]] = [];
                    }
                    allData["_n_" + item[options.parentCode]].push(item);
                }
            });
        }
        // 得到根节点
        target.getRootNodes = function () {
            return allData["_root_"];
        };
        // 递归获取子节点并且设置子节点
        target.handleNode = function (parentNode, lv, tbody) {
            var _ls = allData["_n_" + parentNode[options.code]];
            var tr = target.renderRow(parentNode, _ls ? true : false, lv);
            tbody.append(tr);
            if (_ls) {
                $.each(_ls, function (i, item) {
                    target.handleNode(item, (lv + 1), tbody)
                });
            }
        };
        // 绘制行
        target.renderRow = function (item, isP, lv) {
            // 标记已显示
            item.isShow = true;
            var tr = $('<tr class="tg-' + item[options.parentCode] + '"></tr>');
            var _icon = options.expanderCollapsedClass;
            if (options.expandAll) {
                tr.css("display", "table");
                _icon = options.expanderExpandedClass;
            } else if (options.expandFirst && lv <= 2) {
                tr.css("display", "table");
                _icon = (lv == 1) ? options.expanderExpandedClass : options.expanderCollapsedClass;
            } else {
                tr.css("display", "none");
                _icon = options.expanderCollapsedClass;
            }
            $.each(options.columns, function (index, column) {
                // 判断有没有选择列
                if (index == 0 && column.field == 'selectItem') {
                    hasSelectItem = true;
                    var td = $('<td style="text-align:center;width:36px"></td>');
                    if (column.radio) {
                        var _ipt = $('<input name="select_item" type="radio" value="' + item[options.id] + '"></input>');
                        td.append(_ipt);
                    }
                    if (column.checkbox) {
                        var _ipt = $('<input name="select_item" type="checkbox" value="' + item[options.id] + '"></input>');
                        td.append(_ipt);
                    }
                    tr.append(td);
                } else {
                    var td = $('<td title="' + item[column.field] + '" name="' + column.field + '" style="' + ((column.width) ? ('width:' + column.width) : '') + '"></td>');
                    // 增加formatter渲染
                    if (column.formatter) {
                        td.html(column.formatter.call(this, item[column.field], item, index));
                    } else {
                        td.text(item[column.field]);
                    }
                    if (options.expandColumn == index) {
                        if (!isP) {
                            td.prepend('<span class="treetable-expander"></span>')
                        } else {
                            td.prepend('<span class="treetable-expander ' + _icon + '"></span>')
                        }
                        for (var int = 0; int < (lv - 1); int++) {
                            td.prepend('<span class="treetable-indent"></span>')
                        }
                    }
                    tr.append(td);
                }
            });
            return tr;
        }
        // 加载数据
        target.load = function (parms) {
            // 加载数据前先清空
            allData = {};
            // 加载数据前先清空
            target.html("");
            // 构造表头
            var thr = $('<tr></tr>');
            $.each(options.columns, function (i, item) {
                var th = null;
                // 判断有没有选择列
                if (i == 0 && item.field == 'selectItem') {
                    hasSelectItem = true;
                    th = $('<th style="width:36px"></th>');
                } else {
                    th = $('<th style="' + ((item.width) ? ('width:' + item.width) : '') + '"></th>');
                }
                th.text(item.title);
                thr.append(th);
            });
            var thead = $('<thead class="treetable-thead"></thead>');
            thead.append(thr);
            target.append(thead);
            // 构造表体
            var tbody = $('<tbody class="treetable-tbody"></tbody>');
            target.append(tbody);
            // 添加加载loading
            var _loading = '<tr><td colspan="' + options.columns.length + '"><div style="display: block;text-align: center;">正在努力地加载数据中，请稍候……</div></td></tr>'
            tbody.html(_loading);
            // 默认高度
            if (options.height) {
                tbody.css("height", options.height);
            }
            $.ajax({
                type: options.type,
                url: options.url,
                data: parms ? parms : options.ajaxParams,
                dataType: "JSON",
                success: function (data, textStatus, jqXHR) {
                    // 加载完数据先清空
                    tbody.html("");
                    if (!data || data.length <= 0) {
                        var _empty = '<tr><td colspan="' + options.columns.length + '"><div style="display: block;text-align: center;">没有找到匹配的记录</div></td></tr>'
                        tbody.html(_empty);
                        return;
                    }
                    // 格式化数据
                    target.formatData(data);
                    // 开始绘制
                    var rootNode = target.getRootNodes();
                    if (rootNode) {
                        $.each(rootNode, function (i, item) {
                            target.handleNode(item, 1, tbody);
                        });
                    }
                    // 下边的操作主要是为了查询时让一些没有根节点的节点显示
                    $.each(data, function (i, item) {
                        if (!item.isShow) {
                            var tr = target.renderRow(item, false, 1);
                            tbody.append(tr);
                        }
                    });
                    target.append(tbody);
                    //动态设置表头宽度
                    thead.css("width", tbody.children(":first").css("width"));
                    // 行点击选中事件
                    target.find("tbody").find("tr").click(function () {
                        if (hasSelectItem) {
                            var _ipt = $(this).find("input[name='select_item']");
                            if (_ipt.attr("type") == "radio") {
                                _ipt.prop('checked', true);
                                target.find("tbody").find("tr").removeClass("treetable-selected");
                                $(this).addClass("treetable-selected");
                            } else {
                                if (_ipt.prop('checked')) {
                                    _ipt.prop('checked', false);
                                    $(this).removeClass("treetable-selected");
                                } else {
                                    _ipt.prop('checked', true);
                                    $(this).addClass("treetable-selected");
                                }
                            }
                        }
                    });
                    // 小图标点击事件--展开缩起
                    target.find("tbody").find("tr").find(".treetable-expander").click(function () {
                        var tr = $(this).parent().parent();
                        var _code = tr.find("input[name='select_item']").val();
                        if (options.id == options.code) {
                            _code = tr.find("input[name='select_item']").val();
                        } else {
                            _code = tr.find("td[name='" + options.code + "']").text();
                        }
                        var _ls = target.find("tbody").find(".tg-" + _code);//下一级，改为下所有级别
                        if (_ls && _ls.length > 0) {
                            var _flag = $(this).hasClass(options.expanderExpandedClass);
                            $.each(_ls, function (index, item) {

                                //查找当前这个节点的所有节点(包含子节点)，如果是折叠都显示为不显示，如果是展开，则根据当前节点的状态
                                extracted($, item, target, _flag, options);

                                $(item).css("display", _flag ? "none" : "table");
                            });
                            if (_flag) {
                                $(this).removeClass(options.expanderExpandedClass)
                                $(this).addClass(options.expanderCollapsedClass)
                            } else {
                                $(this).removeClass(options.expanderCollapsedClass)
                                $(this).addClass(options.expanderExpandedClass)
                            }
                        }
                    });
                },
                error: function (xhr, textStatus) {
                    var _errorMsg = '<tr><td colspan="' + options.columns.length + '"><div style="display: block;text-align: center;">' + xhr.responseText + '</div></td></tr>'
                    tbody.html(_errorMsg);
                    debugger;
                },
            });
        }
        if (options.url) {
            target.load();
        } else {
            // 也可以通过defaults里面的data属性通过传递一个数据集合进来对组件进行初始化....有兴趣可以自己实现，思路和上述类似
        }

        return target;
    };

    // 组件方法封装........
    $.fn.bootstrapTreeTable.methods = {
        // 返回选中记录的id（返回的id由配置中的id属性指定）
        // 为了兼容bootstrap-table的写法，统一返回数组，这里只返回了指定的id
        getSelections: function (target, data) {
            // 所有被选中的记录input
            var _ipt = target.find("tbody").find("tr").find("input[name='select_item']:checked");
            var chk_value = [];
            // 如果是radio
            if (_ipt.attr("type") == "radio") {
                var _data = {id: _ipt.val()};
                var _tds = _ipt.parent().parent().find("td");
                _tds.each(function (_i, _item) {
                    if (_i != 0) {
                        _data[$(_item).attr("name")] = $(_item).text();
                    }
                });
                chk_value.push(_data);
            } else {
                _ipt.each(function (_i, _item) {
                    var _data = {id: $(_item).val()};
                    var _tds = $(_item).parent().parent().find("td");
                    _tds.each(function (_ii, _iitem) {
                        if (_ii != 0) {
                            _data[$(_iitem).attr("name")] = $(_iitem).text();
                        }
                    });
                    chk_value.push(_data);
                });
            }
            return chk_value;
        },
        // 刷新记录
        refresh: function (target, parms) {
            if (parms) {
                target.load(parms);
            } else {
                target.load();
            }
        },
        // 组件的其他方法也可以进行类似封装........
    };

    $.fn.bootstrapTreeTable.defaults = {
        id: 'id',// 选取记录返回的值
        code: 'id',// 用于设置父子关系
        parentCode: 'parentId',// 用于设置父子关系
        rootCodeValue: null,//设置根节点code值----可指定根节点，默认为null,"",0,"0"
        data: [], // 构造table的数据集合
        type: "GET", // 请求数据的ajax类型
        url: null, // 请求数据的ajax的url
        ajaxParams: {}, // 请求数据的ajax的data属性
        expandColumn: null,// 在哪一列上面显示展开按钮
        expandAll: true, // 是否全部展开
        expandFirst: false, // 是否默认第一级展开--expandAll为false时生效
        striped: false, // 是否各行渐变色
        columns: [],
        toolbar: null,//顶部工具条
        height: 0,
        expanderExpandedClass: 'glyphicon glyphicon-chevron-down',// 展开的按钮的图标
        expanderCollapsedClass: 'glyphicon glyphicon-chevron-right'// 缩起的按钮的图标

    };
})(jQuery);