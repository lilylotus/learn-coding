## 确认消息接收

### 1. basicAck

`basicAck`：表示成功确认，使用此回执方法后，消息会被 `rabbitmq broker` 删除。

`void basicAck(long deliveryTag, boolean multiple)`

- `deliveryTag`：表示消息投递序号，每次消费消息或者消息重新投递后，`deliveryTag` 都会增加。手动消息确认模式下，可以对指定 `deliveryTag` 的消息进行 `ack`、`nack`、`reject` 等操作。
- `multiple`：是否批量确认，值为 true 则会一次性 ack 所有小于当前消息 `deliveryTag` 的消息。

### 2. basicNack

`basicNack` ：表示失败确认，一般在消费消息业务异常时用到此方法，可以将消息重新投递入队列。

`void basicNack(long deliveryTag, boolean multiple, boolean requeue)`

- `deliveryTag`：表示消息投递序号。
- `multiple`：是否批量确认。
- `requeue`：值为 `true` 消息将重新入队列。

**注意：** 若是队列和死信队列关联，当 `requeue` 为 `false` 时，消息会投递到死信队列当中，由私信队列的消费者消费

### 3. basicReject

`basicReject`：拒绝消息，与 `basicNack` 区别在于不能进行批量操作，其它用法很相似

`void basicReject(long deliveryTag, boolean requeue)`

- `deliveryTag`：表示消息投递序号。
- `requeue`：值为 `true` 消息将重新入队列。

## 问题

### 问题1

> Shutdown Signal: channel error; protocol method: #method<channel.close>(reply-code=406, reply-text=PRECONDITION_FAILED - unknown delivery tag 1, class-id=60, method-id=80)

`rabbitmq` 的消息消费时程序手动消费，但没有开启手动消费，自动确认了一遍，手动又确认了一遍，产生同一个消息被确认消费两次的问题。

```yaml
spring:
  rabbitmq:
    listener:
      type: SIMPLE
      simple:
        # 消费的确认模式为手工确认
        acknowledgeMode: MANUAL
        defaultRequeueRejected: false
```