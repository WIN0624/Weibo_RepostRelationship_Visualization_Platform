var myChart = echarts.init(document.getElementById('main'));
myChart.showLoading();

//分层实现十六进制颜色固定
function color16(node){
    var color;
    if(node.level==0){
        color='#dc143c';
    }else if (node.level==1){
        color='#c71969';
    }else if (node.level==2){
        color='#4f19c7';
    }else{
        color='#000';
    }
    return color;
}

// 待数据完善后，添加展示粉丝数,用于给series的data中的name赋值
function tip(node){
    return node.fs_screen_name
}

// （未实现）想要一个方法去除json数据中fs_screen_name重复以及fs_screen_name为Null的数据,否则可视化图表会报错
  
// json中fs_screen_name：转发用户名、粉丝数、reposts_count：转发量、level：转发层级
// 如果要显示源博主，目前需要手动添加第0层的节点
$.getJSON('data.json', function (json) {
    myChart.hideLoading();
    myChart.setOption(option = {
        title: {
            text: 'Repost Relationship'
        },
        animation: false,
        series : [
            { 
                type: 'graph',
                layout: 'force',
                data: json.nodes.map(function (node) {
                    return {
                        id: node.fs_screen_name,
                        name: node.fs_screen_name,
                        // 待数据完善，使用tip(node)方法显示name
                        symbolSize: 50,
                        // 待数据完善，用粉丝数确定圆的大小
                        itemStyle: {
                            color:color16(node)
                        },
                    }
                }),
                edges: json.nodes.map(function (edge) {
                    return {
                        source: edge.screen_name,
                        target: edge.fs_screen_name
                    };
                }),
                emphasis: {
                    label: {
                        position: 'right',
                        show: true
                    }
                },
                roam: true,
                focusNodeAdjacency: true,
                lineStyle: {
                    width: 0.8,
                    curveness: 0.3,
                    opacity: 0.7
                },
                itemStyle: {
                    borderColor: '#fff',
                    borderWidth: 1,
                    shadowBlur: 10,
                    shadowColor: 'rgba(0, 0, 0, 0.3)'
                },
                force: {
                    edgeLength: 80,
                    repulsion: 500,
                    gravity: 0.2
                },
               draggable:true
            }
        ]
    }, true);
});