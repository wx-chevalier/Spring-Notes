# OSS

# 客户端文件上传

## 客户端直传

![](https://s2.ax1x.com/2019/09/05/nu1JJA.md.png)

首先从服务端获取 OSS 令牌，用的 browser 版本 sdk 的 ali-oss，根据 OSS 相关信息创建 ossClient，然后使用类似 `ossClient.put(path, file)` 的方法上传，并更新 Upload 里的 fileList。首先构建 ossClientCreator：

```js
const OSS = require("ali-oss");

export interface IOssConfig {
  accessKeyId: string;
  accessKeySecret: string;
  securityToken: string;
  region: string;
  bucket: string;
  endpoint: string;
  [propName: string]: any;
}

export default (ossConfig: IOssConfig) => {
  return new OSS({
    accessKeyId: ossConfig.accessKeyId,
    accessKeySecret: ossConfig.accessKeySecret,
    stsToken: ossConfig.securityToken,
    region: ossConfig.region,
    bucket: ossConfig.bucket
  });
};
```

```js
import http from '../httpService';
import { IOssConfig } from 'src/utils/ossClientCreator';

export async function getOssConfig(): Promise<IOssConfig> {
  let res = await http.post('/api/services/app/Util/OssToken');
  return res.data.result;
}

export async function multipartUpload(ossClient: any, path: string, file: File) {
  let ossRes = await ossClient.multipartUpload(path, file);
  let url: string = '';
  if (ossRes && ossRes.name) {
    url = `http://${ossClient.options.bucket}.${ossClient.options.endpoint.host}/${ossRes.name}`;
  } else {
    throw new Error('上传失败');
  }
  ossClient = null;
  return url;
}

export async function put(ossClient: any, path: string, file: File) {
  let ossRes = await ossClient.put(path, file);

  let url: string = '';
  if (ossRes && ossRes.name) {
    url = `http://${ossClient.options.bucket}.${ossClient.options.endpoint.host}/${ossRes.name}`;
  } else {
    throw new Error('上传失败');
  }
  ossClient = null;
  return url;
}

export async function upload(ossClient: any, path: string, file: File) {
  let size = file.size / 1024;
  if (size > 100 * 1024) {
   throw new Error( '附件大小不能超过100M')；
  }
  if (size > 50 * 1024) {
    return await multipartUpload(ossClient, path, file);
  }
  let result = await ossClient.put(path, file);
  // console.log(result, '上传成功');
  let url: string = '';
  if (result && result.name) {
    url = `http://${ossClient.options.bucket}.${ossClient.options.endpoint.host}/${result.name}`;
  } else {
    throw new Error('上传失败');
  }
  ossClient = null;
  return url;
}

export default {
  getOssConfig,
  multipartUpload,
  put,
  upload,
};
```

在上传组件的加载完毕的事件中，加载 OSS 配置：

```js
async componentDidMount() {
    const config = await ossService.getOssConfig();
    this.client = ossClientCreator(config);
  }
```

然后如下封装 Antd 的 Upload 组件：

```js
<Upload
  customRequest={(e: any) => {
    if (!this.client) return;
    ossService
      .put(
        this.client,
        "upload/" +
          e.file.uid.replace(/-/g, "") +
          "." +
          e.file.type.match(/image\/(\w*)/)[1],
        e.file
      )
      .then((url: any) => {
        let index = this.state.fileList.findIndex(
          (n: UploadFile) => n.uid === e.file.uid
        );
        if (index > -1) {
          this.state.fileList[index].url = url;
          this.state.fileList[index].status = "done";
        } else {
          this.state.fileList.push({
            ...e.file,
            status: "done",
            url: url
          });
        }
        const { onUpdate } = this.props;
        if (onUpdate) {
          onUpdate(this.state.fileList);
        }
        this.setState({
          fileList: this.state.fileList
        });
      });
  }}
  listType="picture-card"
  fileList={fileList}
  onPreview={this.handlePreview}
  onChange={this.handleChange}
  onRemove={this.handleRemove}
  multiple={true}
>
  {uploadButton}
</Upload>
```

## 服务端签名后直传

采用 JavaScript 客户端直接签名时，AccessKeyID 和 AcessKeySecret 会暴露在前端页面，因此存在严重的安全隐患。因此，OSS 提供了服务端签名后直传的方案。

![非直传架构](https://s2.ax1x.com/2019/09/16/nfFKpD.png)

服务端签名后直传的原理如下：用户发送上传 Policy 请求到应用服务器、应用服务器返回上传 Policy 和签名给用户、用户直接上传数据到 OSS。

- 签名直传服务

签名直传服务响应客户端发送给应用服务器的 GET 消息。

```java
protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

    String accessId = "<yourAccessKeyId>"; // 请填写您的AccessKeyId。
    String accessKey = "<yourAccessKeySecret>"; // 请填写您的AccessKeySecret。
    String endpoint = "oss-cn-hangzhou.aliyuncs.com"; // 请填写您的 endpoint。
    String bucket = "bucket-name"; // 请填写您的 bucketname 。
    String host = "https://" + bucket + "." + endpoint; // host的格式为 bucketname.endpoint
    // callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
    String callbackUrl = "http://88.88.88.88:8888";
    String dir = "user-dir-prefix/"; // 用户上传文件时指定的前缀。

    OSSClient client = new OSSClient(endpoint, accessId, accessKey);
    try {
        long expireTime = 30;
        long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
        Date expiration = new Date(expireEndTime);
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

        String postPolicy = client.generatePostPolicy(expiration, policyConds);
        byte[] binaryData = postPolicy.getBytes("utf-8");
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = client.calculatePostSignature(postPolicy);

        Map<String, String> respMap = new LinkedHashMap<String, String>();
        respMap.put("accessid", accessId);
        respMap.put("policy", encodedPolicy);
        respMap.put("signature", postSignature);
        respMap.put("dir", dir);
        respMap.put("host", host);
        respMap.put("expire", String.valueOf(expireEndTime / 1000));
        // respMap.put("expire", formatISO8601Date(expiration));

        JSONObject jasonCallback = new JSONObject();
        jasonCallback.put("callbackUrl", callbackUrl);
        jasonCallback.put("callbackBody",
                "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
        jasonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");
        String base64CallbackBody = BinaryUtil.toBase64String(jasonCallback.toString().getBytes());
        respMap.put("callback", base64CallbackBody);

        JSONObject ja1 = JSONObject.fromObject(respMap);
        // System.out.println(ja1.toString());
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST");
        response(request, response, ja1.toString());

    } catch (Exception e) {
        // Assert.fail(e.getMessage());
        System.out.println(e.getMessage());
    }
}
```

在前端使用中，即将返回的前面参数拼接到请求服务中：

```js
{
  'key' : g_object_name,
  'policy': policyBase64,
  'OSSAccessKeyId': accessid,
  'success_action_status' : '200', //让服务端返回200,不然，默认会返回204
  'callback' : callbackbody,
  'signature': signature
}
```

- 上传回调服务

上传回调服务响应 OSS 发送给应用服务器的 POST 消息。

```java
protected boolean VerifyOSSCallbackRequest(HttpServletRequest request, String ossCallbackBody)
        throws NumberFormatException, IOException {
    boolean ret = false;
    String autorizationInput = new String(request.getHeader("Authorization"));
    String pubKeyInput = request.getHeader("x-oss-pub-key-url");
    byte[] authorization = BinaryUtil.fromBase64String(autorizationInput);
    byte[] pubKey = BinaryUtil.fromBase64String(pubKeyInput);
    String pubKeyAddr = new String(pubKey);
    if (!pubKeyAddr.startsWith("https://gosspublic.alicdn.com/")
            && !pubKeyAddr.startsWith("https://gosspublic.alicdn.com/")) {
        System.out.println("pub key addr must be oss addrss");
        return false;
    }
    String retString = executeGet(pubKeyAddr);
    retString = retString.replace("-----BEGIN PUBLIC KEY-----", "");
    retString = retString.replace("-----END PUBLIC KEY-----", "");
    String queryString = request.getQueryString();
    String uri = request.getRequestURI();
    String decodeUri = java.net.URLDecoder.decode(uri, "UTF-8");
    String authStr = decodeUri;
    if (queryString != null && !queryString.equals("")) {
        authStr += "?" + queryString;
    }
    authStr += "\n" + ossCallbackBody;
    ret = doCheck(authStr, authorization, retString);
    return ret;
}

protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    String ossCallbackBody = GetPostBody(request.getInputStream(),
            Integer.parseInt(request.getHeader("content-length")));
    boolean ret = VerifyOSSCallbackRequest(request, ossCallbackBody);
    System.out.println("verify result : " + ret);
    // System.out.println("OSS Callback Body:" + ossCallbackBody);
    if (ret) {
        response(request, response, "{\"Status\":\"OK\"}", HttpServletResponse.SC_OK);
    } else {
        response(request, response, "{\"Status\":\"verdify not ok\"}", HttpServletResponse.SC_BAD_REQUEST);
    }
}
```
