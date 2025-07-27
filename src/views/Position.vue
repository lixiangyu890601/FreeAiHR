<template>
    <div class="position-container">
        <el-card class="position-card">
            <template #header>
                <Header title="人才管理" />
            </template>
            <div class="operation-area">
                <el-form :inline="true" :model="searchForm" @keyup.enter="handleSearch" class="search-form">
                    <el-form-item label="岗位名称">
                        <el-input v-model="searchForm.name" placeholder="请输入岗位名称" clearable style="width: 150px" />
                    </el-form-item>
                    <el-form-item label="所属部门">
                        <el-input v-model="searchForm.department" placeholder="请输入所属部门" clearable
                            style="width: 150px" />
                    </el-form-item>

                    <el-form-item label="状态">
                        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
                            <el-option label="招聘中" value="招聘中" />
                            <el-option label="已结束" value="已结束" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="handleSearch">搜索</el-button>
                        <el-button @click="handleReset">重置</el-button>
                    </el-form-item>
                </el-form>
                <div class="button-area">
                    <el-button type="primary" @click="handleAdd">新增岗位</el-button>
                </div>
            </div>
            <el-table :data="paginatedData" style="width: 100%">
                <el-table-column label="序号" width="80">
                    <template #default="{ $index }">
                        {{ (currentPage - 1) * pageSize + $index + 1 }}
                    </template>
                </el-table-column>
                <el-table-column prop="name" label="岗位名称" />
                <el-table-column prop="department" label="所属部门" />
                <el-table-column prop="count" label="招聘人数" width="100" />
                <el-table-column prop="hiredCount" label="已招聘人数" width="100" />
                <el-table-column prop="salary" label="薪资范围" width="150" />
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="row.status === '招聘中' ? 'success' : 'info'">
                            {{ row.status }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180" />
                <el-table-column label="操作" width="200">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
                        <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination-container">
                <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="positionList.length"
                    layout="total, prev, pager, next, jumper" @current-change="handlePageChange" />
            </div>
        </el-card>

        <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增岗位' : '编辑岗位'" width="500px">
            <el-form :model="positionForm" :rules="rules" ref="positionFormRef" label-width="100px">
                <el-form-item label="岗位名称" prop="name">
                    <el-input v-model="positionForm.name" placeholder="请输入岗位名称" />
                </el-form-item>
                <el-form-item label="所属部门" prop="department">
                    <el-input v-model="positionForm.department" placeholder="请输入所属部门" />
                </el-form-item>
                <el-form-item label="招聘人数" prop="count">
                    <el-input-number v-model="positionForm.count" :min="1" :max="999" />
                </el-form-item>
                <el-form-item label="薪资范围" prop="salary">
                    <el-input v-model="positionForm.salary" placeholder="请输入薪资范围" />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-select v-model="positionForm.status" placeholder="请选择状态">
                        <el-option label="招聘中" value="招聘中" />
                        <el-option label="已结束" value="已结束" />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleSubmit">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Header from '../components/Header.vue'

const currentPage = ref(1)
const pageSize = 10
const dialogVisible = ref(false)
const dialogType = ref('add')
const positionFormRef = ref(null)

const positionForm = ref({
    name: '',
    department: '',
    count: 1,
    hiredCount: 0,
    salary: '',
    status: '招聘中'
})

const rules = {
    name: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
    department: [{ required: true, message: '请输入所属部门', trigger: 'blur' }],
    count: [{ required: true, message: '请输入招聘人数', trigger: 'blur' }],
    salary: [{ required: true, message: '请输入薪资范围', trigger: 'blur' }],
    status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const positionList = ref([
    {
        id: 1,
        name: 'Java高级工程师',
        department: '技术部',
        count: 2,
        hiredCount: 1,
        salary: '25k-35k',
        status: '招聘中',
        createTime: '2024-03-15 10:30:00'
    },
    {
        id: 2,
        name: '前端工程师',
        department: '技术部',
        count: 1,
        hiredCount: 0,
        salary: '15k-25k',
        status: '招聘中',
        createTime: '2024-03-14 15:45:00'
    },
    {
        id: 3,
        name: '产品经理',
        department: '产品部',
        count: 1,
        hiredCount: 1,
        salary: '20k-30k',
        status: '已结束',
        createTime: '2024-03-13 09:15:00'
    },
    {
        id: 4,
        name: 'UI设计师',
        department: '设计部',
        count: 2,
        hiredCount: 0,
        salary: '12k-18k',
        status: '招聘中',
        createTime: '2024-03-12 14:20:00'
    },
    {
        id: 5,
        name: '运维工程师',
        department: '运维部',
        count: 1,
        hiredCount: 0,
        salary: '15k-22k',
        status: '招聘中',
        createTime: '2024-03-11 16:30:00'
    },
    {
        id: 6,
        name: '数据分析师',
        department: '数据部',
        count: 2,
        hiredCount: 1,
        salary: '18k-25k',
        status: '招聘中',
        createTime: '2024-03-10 09:45:00'
    },
    {
        id: 7,
        name: '测试工程师',
        department: '测试部',
        count: 3,
        hiredCount: 1,
        salary: '12k-20k',
        status: '招聘中',
        createTime: '2024-03-09 11:20:00'
    },
    {
        id: 8,
        name: '后端工程师',
        department: '技术部',
        count: 2,
        hiredCount: 0,
        salary: '20k-30k',
        status: '招聘中',
        createTime: '2024-03-08 15:40:00'
    },
    {
        id: 9,
        name: '市场专员',
        department: '市场部',
        count: 1,
        hiredCount: 1,
        salary: '8k-12k',
        status: '已结束',
        createTime: '2024-03-07 10:15:00'
    },
    {
        id: 10,
        name: 'HR专员',
        department: '人力资源部',
        count: 1,
        hiredCount: 0,
        salary: '8k-12k',
        status: '招聘中',
        createTime: '2024-03-06 14:25:00'
    },
    {
        id: 11,
        name: '算法工程师',
        department: '技术部',
        count: 2,
        hiredCount: 0,
        salary: '25k-40k',
        status: '招聘中',
        createTime: '2024-03-05 16:50:00'
    },
    {
        id: 12,
        name: '运营经理',
        department: '运营部',
        count: 1,
        hiredCount: 1,
        salary: '20k-30k',
        status: '已结束',
        createTime: '2024-03-04 09:30:00'
    },
    {
        id: 13,
        name: '财务主管',
        department: '财务部',
        count: 1,
        hiredCount: 0,
        salary: '15k-25k',
        status: '招聘中',
        createTime: '2024-03-03 11:45:00'
    },
    {
        id: 14,
        name: '销售经理',
        department: '销售部',
        count: 2,
        hiredCount: 1,
        salary: '15k-25k',
        status: '招聘中',
        createTime: '2024-03-02 14:20:00'
    },
    {
        id: 15,
        name: '客服专员',
        department: '客服部',
        count: 3,
        hiredCount: 2,
        salary: '6k-8k',
        status: '招聘中',
        createTime: '2024-03-01 10:15:00'
    },
    {
        id: 16,
        name: '架构师',
        department: '技术部',
        count: 1,
        hiredCount: 0,
        salary: '35k-50k',
        status: '招聘中',
        createTime: '2024-02-29 15:30:00'
    },
    {
        id: 17,
        name: '行政助理',
        department: '行政部',
        count: 1,
        hiredCount: 1,
        salary: '6k-8k',
        status: '已结束',
        createTime: '2024-02-28 09:20:00'
    },
    {
        id: 18,
        name: '采购专员',
        department: '采购部',
        count: 1,
        hiredCount: 0,
        salary: '8k-12k',
        status: '招聘中',
        createTime: '2024-02-27 14:40:00'
    },
    {
        id: 19,
        name: '品牌经理',
        department: '市场部',
        count: 1,
        hiredCount: 0,
        salary: '20k-30k',
        status: '招聘中',
        createTime: '2024-02-26 11:25:00'
    },
    {
        id: 20,
        name: '安全工程师',
        department: '技术部',
        count: 1,
        hiredCount: 0,
        salary: '20k-30k',
        status: '招聘中',
        createTime: '2024-02-25 16:15:00'
    },
    {
        id: 21,
        name: '法务专员',
        department: '法务部',
        count: 1,
        hiredCount: 1,
        salary: '15k-25k',
        status: '已结束',
        createTime: '2024-02-24 10:30:00'
    },
    {
        id: 22,
        name: '内容运营',
        department: '运营部',
        count: 2,
        hiredCount: 1,
        salary: '10k-15k',
        status: '招聘中',
        createTime: '2024-02-23 14:50:00'
    },
    {
        id: 23,
        name: '数据库工程师',
        department: '技术部',
        count: 1,
        hiredCount: 0,
        salary: '20k-30k',
        status: '招聘中',
        createTime: '2024-02-22 09:15:00'
    },
    {
        id: 24,
        name: '商务专员',
        department: '商务部',
        count: 2,
        hiredCount: 1,
        salary: '10k-15k',
        status: '招聘中',
        createTime: '2024-02-21 15:40:00'
    },
    {
        id: 25,
        name: '培训讲师',
        department: '人力资源部',
        count: 1,
        hiredCount: 0,
        salary: '15k-20k',
        status: '招聘中',
        createTime: '2024-02-20 11:20:00'
    }
])

const searchForm = ref({
    name: '',
    department: '',
    salary: '',
    status: ''
})

const filteredList = computed(() => {
    return positionList.value.filter(item => {
        const nameMatch = !searchForm.value.name || item.name.includes(searchForm.value.name)
        const departmentMatch = !searchForm.value.department || item.department.includes(searchForm.value.department)
        const salaryMatch = !searchForm.value.salary || item.salary.includes(searchForm.value.salary)
        const statusMatch = !searchForm.value.status || item.status === searchForm.value.status
        return nameMatch && departmentMatch && salaryMatch && statusMatch
    })
})

const paginatedData = computed(() => {
    const start = (currentPage.value - 1) * pageSize
    const end = start + pageSize
    return filteredList.value.slice(start, end)
})

const handleSearch = () => {
    currentPage.value = 1
}

const handleReset = () => {
    searchForm.value = {
        name: '',
        department: '',
        salary: '',
        status: ''
    }
    currentPage.value = 1
}

const handlePageChange = (page) => {
    currentPage.value = page
}

const handleAdd = () => {
    dialogType.value = 'add'
    positionForm.value = {
        name: '',
        department: '',
        count: 1,
        salary: '',
        status: '招聘中'
    }
    dialogVisible.value = true
}

const handleEdit = (row) => {
    dialogType.value = 'edit'
    positionForm.value = { ...row }
    dialogVisible.value = true
}

const handleDelete = (row) => {
    ElMessageBox.confirm('确认删除该岗位？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        positionList.value = positionList.value.filter(item => item.id !== row.id)
        ElMessage.success('删除成功')
    }).catch(() => { })
}

const handleSubmit = () => {
    positionFormRef.value.validate((valid) => {
        if (valid) {
            if (dialogType.value === 'add') {
                const newPosition = {
                    ...positionForm.value,
                    id: positionList.value.length + 1,
                    createTime: new Date().toLocaleString()
                }
                positionList.value.unshift(newPosition)
                ElMessage.success('添加成功')
            } else {
                const index = positionList.value.findIndex(item => item.id === positionForm.value.id)
                if (index !== -1) {
                    positionList.value[index] = { ...positionForm.value }
                    ElMessage.success('修改成功')
                }
            }
            dialogVisible.value = false
        }
    })
}
</script>

<style scoped>
.position-container {
    height: 100%;
    padding: 100px 10px 20px;
    width: 100%;
    margin: 0 auto;
    box-sizing: border-box;
}

.operation-area {
    margin-bottom: 10px;
    display: flex;
    flex-direction: column;
    gap: 5px;
}

.button-area {
    display: flex;
    justify-content: flex-start;
}

.search-form {
    background-color: var(--el-fill-color-light);
    padding: 8px;
    border-radius: 4px;
    flex: 1;
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    margin: 0;
}

.position-card {
    height: 100%;
    display: flex;
    flex-direction: column;
}

.position-card :deep(.el-card__body) {
    flex: 1;
    overflow: hidden;
    display: flex;
    flex-direction: column;
}

.position-card :deep(.el-table) {
    flex: 1;
    overflow: auto;
    --el-table-border-color: var(--el-border-color-lighter);
    --el-table-border: 1px solid var(--el-table-border-color);
    border: var(--el-table-border);
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

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}
</style>