<template>
    <el-container class="layout-container">
        <el-aside :width="isCollapse ? '64px' : '200px'" class="aside">
            <el-menu class="menu" :router="true" default-active="/upload" :collapse="isCollapse">
                <el-menu-item index="/upload">
                    <el-icon><upload-filled /></el-icon>
                    <span>上传简历</span>
                </el-menu-item>
                <el-menu-item index="/fileList">
                    <el-icon>
                        <document />
                    </el-icon>
                    <span>简历列表</span>
                </el-menu-item>
                <el-menu-item index="/position">
                    <el-icon>
                        <briefcase />
                    </el-icon>
                    <span>岗位管理</span>
                </el-menu-item>
            </el-menu>
            <div class="collapse-btn" @click="toggleCollapse">
                <el-icon :class="{ 'is-collapsed': isCollapse }">
                    <arrow-left />
                </el-icon>
            </div>
        </el-aside>
        <el-main class="main" :style="{ marginLeft: isCollapse ? '64px' : '200px' }">
            <router-view />
        </el-main>
    </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { UploadFilled, Document, ArrowLeft, Briefcase } from '@element-plus/icons-vue'

const isCollapse = ref(false)

const toggleCollapse = () => {
    isCollapse.value = !isCollapse.value
}
</script>

<style scoped>
.layout-container {
    height: 100vh;
}

.aside {
    background-color: #304156;
    height: 100%;
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    transition: width 0.3s;
}

.main {
    padding: 20px;
    background-color: #f0f2f5;
    min-height: 100vh;
    transition: margin-left 0.3s;
}

.menu {
    border-right: none;
    background-color: transparent;
    margin-top: 80px;
}

.logo {
    height: 60px;
    line-height: 60px;
    text-align: center;
    color: #fff;
    overflow: hidden;
    margin-bottom: 20px;
}

.collapse-btn {
    position: absolute;
    top: 20px;
    right: 0;
    cursor: pointer;
    color: #bfcbd9;
    transition: color 0.3s;
    background-color: #263445;
    padding: 8px;
    margin-right: 10px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.collapse-btn:hover {
    color: #409eff;
    background-color: #1f2d3d;
}

.collapse-btn .el-icon {
    font-size: 18px;
    transition: transform 0.3s;
}

.collapse-btn .el-icon.is-collapsed {
    transform: rotate(180deg);
}

:deep(.el-menu) {
    background-color: transparent;
}

:deep(.el-menu-item) {
    color: #bfcbd9;
}

:deep(.el-menu-item.is-active) {
    color: #409eff;
    background-color: #263445;
}

:deep(.el-menu-item:hover) {
    background-color: #263445;
}

:deep(.el-menu--collapse) {
    width: 64px;
}
</style>