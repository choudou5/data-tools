# data-tools
数据工具集合

1、AreaUtil.java 爬取国家统计局地区编码

2、BaiduMapUtil.java  经纬度转换 (请使用自己ak)

##全国5级地区编码 (2016最新数据) 
##### <a href="http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/" target="_blank">国家统计局-地区</a>
##### <a href="http://www.stats.gov.cn/tjsj/tjbz/200911/t20091125_8667.html" target="_blank">国家统计局-地区代码规则</a>


level: 1=省份、2=市、3=区县、4=乡镇、5、村、居委会
geo: 百度经纬度
### 示例数据
| id | code | name | pcode | pname | level | geo | fullname | pinyin | pinyin_short | pinyin_first_letter
| -  |:-:   | :----: |
| 729412    |  110000	| 北京市	 | 000000	| 中国	| 1	|  39.92998577808024,116.39564503787867	|  北京市	|  beijingshi	|  bjs	|  b
| 729412	|  120000	|  天津市	|  000000	|  中国	|  1	|  39.143929903310074,117.21081309155257	|  天津市	|  tianjinshi	|  tjs	|  t
| 735317	|  130000	|  河北省	|  000000	|  中国	|  1	|  38.61383974925108,115.66143362422224	|  河北省	|  hebeisheng	|  hbs	|  h

SQL压缩包：<a href="https://share.weiyun.com/843cf3058b5a996b8405e2e1f60c9a1d" target="_blank">dic_area_full.7z</a> (15.6M 共计71.4w)

| Name | Academy | score | 
| - | :-: | -: | 
| Harry Potter | Gryffindor| 90 | 
| Hermione Granger | Gryffindor | 100 | 
| Draco Malfoy | Slytherin | 90 |