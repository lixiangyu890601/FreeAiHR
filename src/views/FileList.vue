<template>
    <div class="file-list-container">
        <el-card class="file-list-card">
            <template #header>
                <Header title="简历管理" />
            </template>
            <div class="operation-area">
                <el-form :inline="true" :model="searchForm" @keyup.enter="handleSearch" class="search-form">
                    <el-form-item label="简历名称">
                        <el-input v-model="searchForm.name" placeholder="请输入简历名称" clearable style="width: 150px" />
                    </el-form-item>
                    <el-form-item label="姓名">
                        <el-input v-model="searchForm.candidateName" placeholder="请输入姓名" clearable
                            style="width: 150px" />
                    </el-form-item>
                    <el-form-item label="手机号">
                        <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable style="width: 150px" />
                    </el-form-item>
                    <el-form-item label="邮箱">
                        <el-input v-model="searchForm.email" placeholder="请输入邮箱" clearable style="width: 150px" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="handleSearch">搜索</el-button>
                        <el-button @click="handleReset">重置</el-button>
                    </el-form-item>
                </el-form>
            </div>
            <el-table :data="paginatedData" style="width: 100%">
                <el-table-column label="序号" width="60">
                    <template #default="{ $index }">
                        {{ (currentPage - 1) * pageSize + $index + 1 }}
                    </template>
                </el-table-column>
                <el-table-column prop="name" label="简历名称" min-width="200" />
                <el-table-column prop="candidateName" label="姓名" width="80" />
                <el-table-column label="手机号" width="140">
                    <template #default="{ row }">
                        {{ maskPhoneNumber(row.phone) }}
                    </template>
                </el-table-column>
                <el-table-column label="邮箱" width="200">
                    <template #default="{ row }">
                        {{ maskEmail(row.email) }}
                    </template>
                </el-table-column>
                <el-table-column prop="uploadTime" label="上传时间" width="160" />
                <el-table-column prop="aiScore" label="AI评分" width="80">
                    <template #default="{ row }">
                        <el-tag :type="getScoreType(row.aiScore)">
                            {{ row.aiScore }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="row.status === '通过筛选' ? 'success' : row.status === '未通过筛选' ? 'danger' : 'info'">
                            {{ row.status }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="120" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="handleView(row)">查看</el-button>
                        <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination-container">
                <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="fileList.length"
                    layout="total, prev, pager, next, jumper" @current-change="handlePageChange" />
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Header from '../components/Header.vue'

const currentPage = ref(1)
const pageSize = 10

const searchForm = ref({
    name: '',
    candidateName: '',
    phone: '',
    email: ''
})

const filteredList = computed(() => {
    return fileList.value.filter(item => {
        const nameMatch = !searchForm.value.name || item.name.toLowerCase().includes(searchForm.value.name.toLowerCase())
        const candidateNameMatch = !searchForm.value.candidateName || item.candidateName.includes(searchForm.value.candidateName)
        const phoneMatch = !searchForm.value.phone || item.phone.includes(searchForm.value.phone)
        const emailMatch = !searchForm.value.email || item.email.toLowerCase().includes(searchForm.value.email.toLowerCase())
        return nameMatch && candidateNameMatch && phoneMatch && emailMatch
    })
})

const handleSearch = () => {
    currentPage.value = 1
}

const handleReset = () => {
    searchForm.value = {
        name: '',
        candidateName: '',
        phone: '',
        email: ''
    }
    currentPage.value = 1
}

const fileList = ref([
    {
        id: 1,
        name: '前端开发工程师简历.pdf',
        candidateName: '张三',
        phone: '13800138000',
        email: 'zhangsan@example.com',
        uploadTime: '2024-01-15 10:30:00',
        status: '通过筛选',
        aiScore: 85
    },
    {
        id: 2,
        name: '后端开发工程师简历.pdf',
        candidateName: '李四',
        phone: '13900139000',
        email: 'lisi@example.com',
        uploadTime: '2024-01-15 11:20:00',
        status: '未通过筛选',
        aiScore: 45
    },
    {
        id: 3,
        name: '产品经理简历.pdf',
        candidateName: '王五',
        phone: '13700137000',
        email: 'wangwu@example.com',
        uploadTime: '2024-01-15 14:15:00',
        status: '待处理',
        aiScore: 75
    },
    {
        id: 4,
        name: 'UI设计师简历.pdf',
        candidateName: '赵六',
        phone: '13600136000',
        email: 'zhaoliu@example.com',
        uploadTime: '2024-01-16 09:30:00',
        status: '通过筛选',
        aiScore: 88
    },
    {
        id: 5,
        name: '运维工程师简历.pdf',
        candidateName: '钱七',
        phone: '13500135000',
        email: 'qianqi@example.com',
        uploadTime: '2024-01-16 11:45:00',
        status: '待处理',
        aiScore: 72
    },
    {
        id: 6,
        name: '数据分析师简历.pdf',
        candidateName: '孙八',
        phone: '13400134000',
        email: 'sunba@example.com',
        uploadTime: '2024-01-16 14:20:00',
        status: '未通过筛选',
        aiScore: 55
    },
    {
        id: 7,
        name: '测试工程师简历.pdf',
        candidateName: '周九',
        phone: '13300133000',
        email: 'zhoujiu@example.com',
        uploadTime: '2024-01-17 10:15:00',
        status: '通过筛选',
        aiScore: 82
    },
    {
        id: 8,
        name: '算法工程师简历.pdf',
        candidateName: '吴十',
        phone: '13200132000',
        email: 'wushi@example.com',
        uploadTime: '2024-01-17 15:40:00',
        status: '待处理',
        aiScore: 78
    }
])

const paginatedData = computed(() => {
    const start = (currentPage.value - 1) * pageSize
    const end = start + pageSize
    return filteredList.value.slice(start, end)
})

const getScoreType = (score) => {
    if (score >= 80) return 'success'
    if (score >= 60) return 'warning'
    return 'danger'
}

const maskPhoneNumber = (phone) => {
    if (!phone) return ''
    return phone.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2')
}

const maskEmail = (email) => {
    if (!email) return ''
    const [username, domain] = email.split('@')
    const maskedUsername = username.length > 2
        ? username.slice(0, 2) + '*'.repeat(username.length - 2)
        : username
    return `${maskedUsername}@${domain}`
}

const handleView = (row) => {
    ElMessage.info('查看简历功能开发中')
}

const handleDelete = (row) => {
    ElMessageBox.confirm('确认删除该简历？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        fileList.value = fileList.value.filter(item => item.id !== row.id)
        ElMessage.success('删除成功')
    }).catch(() => { })
}

const handlePageChange = (page) => {
    currentPage.value = page
}
</script>

<style scoped>
.file-list-container {
    height: 100%;
    padding: 100px 10px 20px;
    width: 100%;
    margin: 0 auto;
    box-sizing: border-box;
}

.operation-area {
    margin-bottom: 10px;
    display: flex;
    align-items: center;
}

.search-form {
    display: flex;
    align-items: center;
    flex-wrap: nowrap;
    gap: 16px;
    margin: 0;
}

.search-form :deep(.el-form-item) {
    margin-bottom: 0;
    margin-right: 0;
}

.search-form :deep(.el-form-item__label) {
    font-size: 14px;
}

.file-list-card {
    height: 100%;
    display: flex;
    flex-direction: column;
}

.file-list-card :deep(.el-card__body) {
    flex: 1;
    overflow: hidden;
    display: flex;
    flex-direction: column;
}

.file-list-card :deep(.el-table) {
    flex: 1;
    overflow: auto;
    --el-table-border-color: var(--el-border-color-lighter);
    --el-table-border: 1px solid var(--el-table-border-color);
    border: var(--el-table-border);
}

.file-list-card :deep(.el-table td) {
    border: var(--el-table-border);
    font-weight: normal;
    background-color: transparent;
}

.file-list-card :deep(.el-table th) {
    border: var(--el-table-border);
    font-weight: bold;
    background-color: #f5f7fa;
}

.file-list-card :deep(.el-table .el-table__fixed-right),
.file-list-card :deep(.el-table .el-table__fixed) {
    background-color: var(--el-bg-color);
    box-shadow: 6px 0 10px -10px rgba(0, 0, 0, 0.12);
}

.file-list-card :deep(.el-table .el-table__fixed-right::before),
.file-list-card :deep(.el-table .el-table__fixed::before) {
    background-color: var(--el-bg-color);
}

.file-list-card :deep(.el-table .el-table__fixed-right td),
.file-list-card :deep(.el-table .el-table__fixed td) {
    background-color: var(--el-bg-color);
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

.header-actions {
    display: flex;
    gap: 10px;
}

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}
</style>