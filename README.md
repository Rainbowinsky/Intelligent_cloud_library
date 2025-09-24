# 智能云图库系统



## 简介：

基于Spring Boot+Redis+COS+Al+WebSocket的企业级智能协同云图库平台。
分为公共图库、私有图库和团队共享图库三大模块。用户可在平台公开上传和检索图片;管理员可以上
传、审核和管理分析图片。个人用户可将图片上传至私有空间进行批量管理、多维检索、编辑和分析;企
业可开通团队空间并邀请成员,共享和实时协同编辑图片
后续更新更多功能，目前准备更新上线


<img width="2538" height="974" alt="屏幕截图 2025-09-24 135429" src="https://github.com/user-attachments/assets/f61f743f-945e-4f22-9326-83fb99621b64" />



### 展示：

首页展示：

![image-20250924135530155](C:\Users\Mr李\AppData\Roaming\Typora\typora-user-images\image-20250924135530155.png)



登陆页面

<img width="2562" height="790" alt="屏幕截图 2025-09-24 135614" src="https://github.com/user-attachments/assets/d61e1a09-0c68-4892-8c9d-d6afc9ecce56" />


空间页面：


<img width="2286" height="805" alt="屏幕截图 2025-09-24 135642" src="https://github.com/user-attachments/assets/24645dfc-9aef-4df2-9543-81c97e3d24d9" />

<img width="2536" height="985" alt="屏幕截图 2025-09-24 140024" src="https://github.com/user-attachments/assets/fd24865e-827a-4307-a528-3a4528a48113" />

项目介绍：

1）所有用户都可以在平台公开上传和检索图片素材，快速找到需要的图片。可用作表情包网站、设计素材网站、壁纸网站等：

2）管理员可以上传、审核和管理图片，并对系统内的图片进行分析：

<img width="2536" height="1136" alt="屏幕截图 2025-09-24 140130" src="https://github.com/user-attachments/assets/20ed2ec6-33cd-434b-88b8-777d63ae33f5" />



3）对于个人用户，可将图片上传至私有空间进行批量管理、检索、编辑和分析，用作个人网盘、个人相册、作品集等：
<img width="2088" height="1193" alt="屏幕截图 2025-09-24 140210" src="https://github.com/user-attachments/assets/3c1b464f-7ff7-41ee-9850-f16ac95cfd94" />

<img width="2308" height="696" alt="屏幕截图 2025-09-24 140227" src="https://github.com/user-attachments/assets/b267774a-eb96-408a-8b73-9694c06ff89b" />

4）对于企业，可开通团队空间并邀请成员，共享图片并 **实时协同编辑图片**，提高团队协作效率。可用于提供商业服务，如企业活动相册、企业内部素材库等：
<img width="2287" height="1206" alt="{E0BE4AB4-9100-40DF-A276-5C29039D9039}" src="https://github.com/user-attachments/assets/6c3d0677-f8e3-4625-9fe0-3bad853ce7ea" />


项目架构设计图：

![img](https://camo.githubusercontent.com/46215679e93c6cc2cc423ccc151a10d7555851b333c5fde2547e7893494710dc/68747470733a2f2f7069632e797570692e6963752f312f313733323639313838393130302d65353632633730392d636666612d343737642d393332392d3164633561633164333563382d32303234313230343134343330343734312d32303234313230343134353334343933352d32303234313230343134353335343233342e706e67)

## 技术选型



### 后端

- Java Spring Boot 框架
- MySQL 数据库 + MyBatis-Plus 框架 + MyBatis X
- Redis 分布式缓存 + Caffeine 本地缓存
- Jsoup 数据抓取
- ⭐️ COS 对象存储
- ⭐️ Sa-Token 权限控制
- ⭐️ WebSocket 双向通信
- ⭐️ Disruptor 高性能无锁队列
- ⭐️ JUC 并发和异步编程
- ⭐️ AI 绘图大模型接入
- ⭐️ 多种设计模式的运用
- ⭐️ 多角度项目优化：性能、成本、安全性等

### 前端

- Vue 3 框架
- Vite 打包工具
- Ant Design Vue 组件库
- Axios 请求库
- Pinia 全局状态管理
- 其他组件：数据可视化、图片编辑等
- ⭐️ 前端工程化：ESLint + Prettier + TypeScript
- ⭐️ OpenAPI 前端代码生成
