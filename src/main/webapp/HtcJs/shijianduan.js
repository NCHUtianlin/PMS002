var dom = document.getElementById("container");
var myChart = echarts.init(dom);
var app = {};
option = null;
option = {
    title: {
        text: '时间段统计'
    },
    tooltip : {
        trigger: 'axis'
    },
    legend: {
        data:['门铃1.1.1','门锁1.1.1','猫眼1.1.1','门铃1.2.1','门锁1.2.1']
    },
    toolbox: {
        feature: {
            saveAsImage: {}
        }
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis : [
        {
            type : 'category',
            boundaryGap : false,
            data : ['一月','二月','三月','四月','五月','六月','七月']
        }
    ],
    yAxis : [
        {
            type : 'value'
        }
    ],
    series : [
        {
            name:'门铃1.1.1',
            type:'line',
            stack: '总量',
            areaStyle: {normal: {}},
            data:[89, 90, 98, 98, 90, 99,98]
        },
        {
            name:'门锁1.1.1',
            type:'line',
            stack: '总量',
            areaStyle: {normal: {}},
            data:[92, 98, 89, 90, 98, 94,93]
        },
        {
            name:'猫眼1.1.1',
            type:'line',
            stack: '总量',
            areaStyle: {normal: {}},
            data:[93, 94, 88, 94, 97, 91,90]
        },
        {
            name:'门铃1.2.1',
            type:'line',
            stack: '总量',
            areaStyle: {normal: {}},
            data:[92, 90, 89, 91, 96, 95,99]
        },
        {
            name:'门锁1.2.1',
            type:'line',
            stack: '总量',
            label: {
                normal: {
                    show: true,
                    position: 'top'
                }
            },
            areaStyle: {normal: {}},
            data:[82, 93, 90, 93, 90, 88, 80]
        }
    ]
};
;
if (option && typeof option === "object") {
    myChart.setOption(option, true);
}