<template>
  <div class="app-layout">
    <div class="sidebar">
      <div class="logo-section">
        <img src="@/assets/logo.png" alt="蚂蚁小智" width="160" height="160" />
        <span class="logo-text">蚂蚁小智（医疗版）</span>
      </div>
      <el-button class="new-chat-button" @click="newChat">
        <i class="fa-solid fa-plus"></i>
        &nbsp;新会话
      </el-button>

      <!-- 历史对话列表 -->
      <div class="history-section">
        <div class="history-title">历史对话</div>
        <div class="history-list">
          <div
            v-for="chat in chatHistory"
            :key="chat.id"
            :class="['history-item', { active: chat.id === currentChatId }]"
            @click="switchChat(chat.id)"
          >
            <div class="history-item-content">
              <span class="history-item-title">{{ chat.title }}</span>
              <span class="history-item-time">{{ formatTime(chat.updateTime) }}</span>
            </div>
            <i
              class="fa-solid fa-trash history-item-delete"
              @click.stop="deleteChat(chat.id)"
            ></i>
          </div>
        </div>
      </div>
    </div>
    <div class="main-content">
      <div class="chat-container">
        <div class="message-list" ref="messaggListRef">
          <div
            v-for="(message, index) in messages"
            :key="index"
            :class="
              message.isUser ? 'message user-message' : 'message bot-message'
            "
          >
            <!-- 会话图标 -->
            <i
              :class="
                message.isUser
                  ? 'fa-solid fa-user message-icon'
                  : 'fa-solid fa-robot message-icon'
              "
            ></i>
            <!-- 会话内容 -->
            <span>
              <span
                v-html="message.isUser ? message.content : renderMarkdown(message.content)"
              ></span>
              <!-- loading -->
              <span
                class="loading-dots"
                v-if="message.isThinking || message.isTyping"
              >
                <span class="dot"></span>
                <span class="dot"></span>
              </span>
            </span>
          </div>
        </div>
        <div class="input-container">
          <el-input
            v-model="inputMessage"
            placeholder="请输入消息"
            @keyup.enter="sendMessage"
          ></el-input>
          <el-button @click="sendMessage" :disabled="isSending" type="primary"
            >发送</el-button
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import axios from 'axios'
import { v4 as uuidv4 } from 'uuid'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'

const messaggListRef = ref()
const isSending = ref(false)
const uuid = ref()
const inputMessage = ref('')
const messages = ref([])
const chatHistory = ref([])
const currentChatId = ref(null)

onMounted(() => {
  initUUID()
  // 配置 marked
  marked.setOptions({
    highlight: function (code, lang) {
      const language = hljs.getLanguage(lang) ? lang : 'plaintext'
      return hljs.highlight(code, { language }).value
    },
    breaks: true,
    gfm: true,
  })
  // 移除 setInterval，改用手动滚动
  watch(messages, () => scrollToBottom(), { deep: true })
  // 加载历史对话列表
  loadChatHistory()
  hello()
})

const scrollToBottom = () => {
  if (messaggListRef.value) {
    messaggListRef.value.scrollTop = messaggListRef.value.scrollHeight
  }
}

const hello = () => {
  sendRequest('你好')
}

const sendMessage = () => {
  if (inputMessage.value.trim()) {
    sendRequest(inputMessage.value.trim())
    inputMessage.value = ''
  }
}

const sendRequest = (message) => {
  isSending.value = true
  const userMsg = {
    isUser: true,
    content: message,
    isTyping: false,
    isThinking: false,
  }
  //第一条默认发送的用户消息”你好“不放入会话列表
  if(messages.value.length > 0){
    messages.value.push(userMsg)
  }


  // 添加机器人加载消息
  const botMsg = {
    isUser: false,
    content: '', // 增量填充
    isTyping: true, // 显示加载动画
    isThinking: false,
  }
  messages.value.push(botMsg)
  const lastMsg = messages.value[messages.value.length - 1]
  scrollToBottom()

  axios
    .post(
      '/api/xiaozhi/chat',
      { memoryId: uuid.value, message },
      {
        responseType: 'stream', // 必须为合法值 "text"
        onDownloadProgress: (e) => {
          const fullText = e.event.target.responseText // 累积的完整文本
          let newText = fullText.substring(lastMsg.content.length)
          lastMsg.content += newText //增量更新
          console.log(lastMsg)
          scrollToBottom() // 实时滚动
        },
      }
    )
    .then(() => {
      // 流结束后隐藏加载动画
      messages.value.at(-1).isTyping = false
      isSending.value = false
      // 自动保存对话
      saveCurrentChat()
    })
    .catch((error) => {
      console.error('流式错误:', error)
      messages.value.at(-1).content = '请求失败，请重试'
      messages.value.at(-1).isTyping = false
      isSending.value = false
    })
}

// 初始化 UUID
const initUUID = () => {
  let storedUUID = localStorage.getItem('user_uuid')
  if (!storedUUID) {
    storedUUID = uuidToNumber(uuidv4())
    localStorage.setItem('user_uuid', storedUUID)
  }
  uuid.value = storedUUID
}

const uuidToNumber = (uuid) => {
  let number = 0
  for (let i = 0; i < uuid.length && i < 6; i++) {
    const hexValue = uuid[i]
    number = number * 16 + (parseInt(hexValue, 16) || 0)
  }
  return number % 1000000
}

// 渲染 Markdown
const renderMarkdown = (content) => {
  if (!content) return ''
  return marked(content)
}

const newChat = () => {
  // 保存当前对话
  if (messages.value.length > 0) {
    saveCurrentChat()
  }

  // 重置状态
  messages.value = []
  localStorage.removeItem('user_uuid')
  initUUID()
  currentChatId.value = null
  hello()
}

// 加载历史对话列表
const loadChatHistory = () => {
  const history = localStorage.getItem('chat_history')
  if (history) {
    chatHistory.value = JSON.parse(history)
  }
}

// 保存当前对话
const saveCurrentChat = () => {
  if (messages.value.length === 0) return

  const chatId = currentChatId.value || Date.now().toString()
  const title = messages.value[0]?.content?.substring(0, 20) || '新对话'

  const chatData = {
    id: chatId,
    title: title,
    messages: messages.value,
    uuid: uuid.value,
    updateTime: Date.now(),
  }

  // 更新或添加到历史记录
  const index = chatHistory.value.findIndex((chat) => chat.id === chatId)
  if (index !== -1) {
    chatHistory.value[index] = chatData
  } else {
    chatHistory.value.unshift(chatData)
  }

  // 限制历史记录数量
  if (chatHistory.value.length > 50) {
    chatHistory.value = chatHistory.value.slice(0, 50)
  }

  localStorage.setItem('chat_history', JSON.stringify(chatHistory.value))
  currentChatId.value = chatId
}

// 切换对话
const switchChat = (chatId) => {
  // 保存当前对话
  if (messages.value.length > 0 && currentChatId.value) {
    saveCurrentChat()
  }

  const chat = chatHistory.value.find((c) => c.id === chatId)
  if (chat) {
    messages.value = chat.messages
    uuid.value = chat.uuid
    currentChatId.value = chat.id
    localStorage.setItem('user_uuid', chat.uuid)
  }
}

// 删除对话
const deleteChat = (chatId) => {
  chatHistory.value = chatHistory.value.filter((chat) => chat.id !== chatId)
  localStorage.setItem('chat_history', JSON.stringify(chatHistory.value))

  // 如果删除的是当前对话，创建新对话
  if (currentChatId.value === chatId) {
    messages.value = []
    localStorage.removeItem('user_uuid')
    initUUID()
    currentChatId.value = null
    hello()
  }
}

// 格式化时间
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`

  return `${date.getMonth() + 1}/${date.getDate()}`
}

</script>
<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 200px;
  background-color: #f4f4f9;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  margin-top: 10px;
}

.new-chat-button {
  width: 100%;
  margin-top: 20px;
}

/* 历史对话样式 */
.history-section {
  width: 100%;
  margin-top: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.history-title {
  font-size: 14px;
  font-weight: bold;
  color: #666;
  margin-bottom: 10px;
  padding: 0 5px;
}

.history-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.history-item {
  padding: 10px;
  background-color: #fff;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid transparent;
}

.history-item:hover {
  background-color: #e8f4f8;
  border-color: #b3d9e8;
}

.history-item.active {
  background-color: #d4edfa;
  border-color: #409eff;
}

.history-item-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  overflow: hidden;
}

.history-item-title {
  font-size: 13px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.history-item-time {
  font-size: 11px;
  color: #999;
}

.history-item-delete {
  color: #999;
  font-size: 12px;
  padding: 5px;
  opacity: 0;
  transition: opacity 0.2s;
}

.history-item:hover .history-item-delete {
  opacity: 1;
}

.history-item-delete:hover {
  color: #f56c6c;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  background-color: #fff;
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
}

.message {
  margin-bottom: 10px;
  padding: 10px;
  border-radius: 4px;
  display: flex;
  /* align-items: center; */
}

.user-message {
  max-width: 70%;
  background-color: #e1f5fe;
  align-self: flex-end;
  flex-direction: row-reverse;
}

.bot-message {
  max-width: 100%;
  background-color: #f1f8e9;
  align-self: flex-start;
}

.message-icon {
  margin: 0 10px;
  font-size: 1.2em;
}

.loading-dots {
  padding-left: 5px;
}

.dot {
  display: inline-block;
  margin-left: 5px;
  width: 8px;
  height: 8px;
  background-color: #000000;
  border-radius: 50%;
  animation: pulse 1.2s infinite ease-in-out both;
}

.dot:nth-child(2) {
  animation-delay: -0.6s;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(0.6);
    opacity: 0.4;
  }

  50% {
    transform: scale(1);
    opacity: 1;
  }
}
.input-container {
  display: flex;
}

.input-container .el-input {
  flex: 1;
  margin-right: 10px;
}

/* Markdown 样式 */
.bot-message :deep(pre) {
  background-color: #282c34;
  padding: 12px;
  border-radius: 6px;
  overflow-x: auto;
  margin: 10px 0;
}

.bot-message :deep(code) {
  font-family: 'Courier New', Courier, monospace;
  font-size: 0.9em;
}

.bot-message :deep(pre code) {
  background-color: transparent;
  padding: 0;
  color: #abb2bf;
}

.bot-message :deep(p code) {
  background-color: #f4f4f4;
  padding: 2px 6px;
  border-radius: 3px;
  color: #e83e8c;
}

.bot-message :deep(h1),
.bot-message :deep(h2),
.bot-message :deep(h3) {
  margin-top: 16px;
  margin-bottom: 8px;
  font-weight: bold;
}

.bot-message :deep(ul),
.bot-message :deep(ol) {
  padding-left: 20px;
  margin: 8px 0;
}

.bot-message :deep(blockquote) {
  border-left: 4px solid #ddd;
  padding-left: 12px;
  margin: 8px 0;
  color: #666;
}

.bot-message :deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin: 10px 0;
}

.bot-message :deep(table th),
.bot-message :deep(table td) {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.bot-message :deep(table th) {
  background-color: #f2f2f2;
  font-weight: bold;
}

/* 媒体查询，当设备宽度小于等于 768px 时应用以下样式 */
@media (max-width: 768px) {
  .main-content {
    padding: 10px 0 10px 0;
  }
  .app-layout {
    flex-direction: column;
  }

  .sidebar {
    /* display: none; */
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    overflow-x: auto;
  }

  .logo-section {
    flex-direction: row;
    align-items: center;
  }

  .logo-text {
    font-size: 20px;
  }

  .logo-section img {
    width: 40px;
    height: 40px;
  }

  .new-chat-button {
    margin-right: 30px;
    width: auto;
    margin-top: 5px;
  }

  /* 移动端隐藏历史对话 */
  .history-section {
    display: none;
  }
}

/* 媒体查询，当设备宽度大于 768px 时应用原来的样式 */
@media (min-width: 769px) {
  .main-content {
    padding: 0 0 10px 10px;
  }

  .app-layout {
    display: flex;
    height: 100vh;
  }

  .sidebar {
    width: 200px;
    background-color: #f4f4f9;
    padding: 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .logo-section {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .logo-text {
    font-size: 18px;
    font-weight: bold;
    margin-top: 10px;
  }

  .new-chat-button {
    width: 100%;
    margin-top: 20px;
  }
}
</style>
