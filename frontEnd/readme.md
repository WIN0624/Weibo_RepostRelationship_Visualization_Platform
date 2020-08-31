#### 1. vue-cli3上传到服务器
在项目根目录下创建vue.config.js
npm run build, 此时生成一个dist文件夹
将生成的dist文件夹部署到服务器的根目录即可

#### 2. vue项目中如何引入外部js文件，并使用其中定义的函数（例如将graph方法引入vue页面并使用）
1. 在编写外部js文件时，需要将功能函数用export抛出
    ```
        function graph(json){
            ......
        }

        export {			//关键
            graph
        }

    ```
2. vue中引入外部js, 注意要在方法外加{}来解构。（如果export后没有defalt，都需要加{}解构）
   ```
        import {graph}  from '...'
   ```
3. 调用。函数前面不需要加this,通常在vue项目中定义的函数在调用时要在前面加上this,但是这里不需要，加上会报错。
   
#### 3. vue-cli3页面跳转样式错位但是刷新又好了的情况
在style中加scoped，注意如果是@import引入的css即使加了scoped也会被编译为全局样式，因此如果要引入，用
    ```
        <style src="..." scoped>
    ```

#### 4. 关于解构
为什么以下代码的data要加{}？
```
    let { data } = await this.$get(
        "/data/hottopic.json"
      );
      this.showList = data;
```
假设不加{}：
```
    let  data  = await this.$get("/data/hottopic.json");
    console.log(data) 
```
此时打印出的结果是一个对象，这个对象中有子对象config, data, headers,request, status等，其中data这个对象中的内容是获取到的数组。所以通过{data}来解构，直接得到我们需要的获取到的数据。
如果不加{}, 则需要：
```
    let res = await this.$get(
        "/data/hottopic.json"
      );
    this.showList=res.data
```