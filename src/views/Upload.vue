<template>
    <div class="upload-container">
        <el-card class="upload-card">
            <template #header>
                <Header title="人才管理" />
            </template>
            <el-upload class="upload-area" drag action="#" :auto-upload="false" :on-change="handleFileChange">
                <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                <div class="el-upload__text">
                    将文件拖到此处，或 <em>点击上传</em>
                </div>
            </el-upload>
            <div class="upload-actions" v-if="fileList.length > 0">
                <el-button type="primary" @click="handleUpload">开始上传</el-button>
                <el-button @click="handleClear">清空列表</el-button>
            </div>
            <el-table v-if="fileList.length > 0" :data="fileList" style="width: 100%; margin-top: 20px;">
                <el-table-column prop="name" label="文件名" />
                <el-table-column prop="size" label="大小" width="180">
                    <template #default="{ row }">
                        {{ formatFileSize(row.size) }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="120">
                    <template #default="{ $index }">
                        <el-button type="danger" link @click="handleRemove($index)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { UploadFilled } from '@element-plus/icons-vue'
import Header from '../components/Header.vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const fileList = ref([])

const handleFileChange = (file) => {
    const isExist = fileList.value.some(item => item.name === file.name)
    if (!isExist) {
        fileList.value.push(file.raw)
    } else {
        ElMessage.warning('文件已存在')
    }
}

const handleUpload = () => {
    ElMessage.success('文件上传成功')
    fileList.value = []
}

const handleRemove = (index) => {
    fileList.value.splice(index, 1)
}

const handleClear = () => {
    fileList.value = []
}

const handleLogout = () => {
    sessionStorage.removeItem('isAuthenticated')
    router.push('/login')
}

const goToFileList = () => {
    router.push('/fileList')
}

const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 B'
    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}
</script>

<style scoped>
.upload-container {
    padding: 100px 30px 20px;
    width: 100%;
    margin: 0 auto;
    box-sizing: border-box;
}

.header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 80px;
    background-color: #fff;
    padding: 0 30px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    z-index: 1000;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 12px;
}

.username {
    font-size: 16px;
    color: #333;
    font-weight: 500;
}

.upload-area {
    width: 100%;
}

.upload-actions {
    margin-top: 20px;
    display: flex;
    gap: 10px;
    justify-content: center;
}

:deep(.el-upload) {
    width: 100%;
}

:deep(.el-upload-dragger) {
    width: 100%;
}
</style>