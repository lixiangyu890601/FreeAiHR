import { DataTypes, Model, Optional } from 'sequelize';
import { sequelize } from '../config/database';
import User from './User';

// 简历属性接口
export interface ResumeAttributes {
  id: number;
  userId: number;
  resumeName: string;
  candidateName: string;
  phone?: string;
  email?: string;
  filePath?: string;
  fileName?: string;
  fileSize?: number;
  aiScore?: number;
  status: 'pending' | 'reviewed' | 'approved' | 'rejected';
  uploadTime: Date;
  reviewTime?: Date;
  reviewerId?: number;
  remarks?: string;
  createdAt: Date;
  updatedAt: Date;
}

// 创建时可选的属性
interface ResumeCreationAttributes extends Optional<ResumeAttributes, 'id' | 'aiScore' | 'reviewTime' | 'reviewerId' | 'remarks' | 'createdAt' | 'updatedAt'> {}

// 简历模型类
class Resume extends Model<ResumeAttributes, ResumeCreationAttributes> implements ResumeAttributes {
  public id!: number;
  public userId!: number;
  public resumeName!: string;
  public candidateName!: string;
  public phone?: string;
  public email?: string;
  public filePath?: string;
  public fileName?: string;
  public fileSize?: number;
  public aiScore?: number;
  public status!: 'pending' | 'reviewed' | 'approved' | 'rejected';
  public uploadTime!: Date;
  public reviewTime?: Date;
  public reviewerId?: number;
  public remarks?: string;
  public createdAt!: Date;
  public updatedAt!: Date;

  // 关联方法
  public getUser!: () => Promise<User>;
  public getReviewer!: () => Promise<User | null>;

  // 静态方法
  static async findByUserId(userId: number): Promise<Resume[]> {
    return this.findAll({
      where: { userId },
      order: [['uploadTime', 'DESC']]
    });
  }

  static async findByStatus(status: string): Promise<Resume[]> {
    return this.findAll({
      where: { status },
      order: [['uploadTime', 'DESC']]
    });
  }

  static async searchResumes(searchTerm: string): Promise<Resume[]> {
    const { Op } = require('sequelize');
    return this.findAll({
      where: {
        [Op.or]: [
          { resumeName: { [Op.like]: `%${searchTerm}%` } },
          { candidateName: { [Op.like]: `%${searchTerm}%` } },
          { email: { [Op.like]: `%${searchTerm}%` } },
          { phone: { [Op.like]: `%${searchTerm}%` } }
        ]
      },
      order: [['uploadTime', 'DESC']]
    });
  }
}

// 定义模型
Resume.init(
  {
    id: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true,
    },
    userId: {
      type: DataTypes.INTEGER,
      allowNull: false,
      references: {
        model: 'users',
        key: 'id'
      }
    },
    resumeName: {
      type: DataTypes.STRING(255),
      allowNull: false,
      validate: {
        notEmpty: {
          msg: 'Resume name is required'
        },
        len: {
          args: [1, 255],
          msg: 'Resume name must be between 1 and 255 characters'
        }
      },
      set(value: string) {
        // Trim whitespace
        this.setDataValue('resumeName', value ? value.trim() : value);
      }
    },
    candidateName: {
      type: DataTypes.STRING(100),
      allowNull: false,
      validate: {
        notEmpty: {
          msg: 'Candidate name is required'
        },
        len: {
          args: [1, 100],
          msg: 'Candidate name must be between 1 and 100 characters'
        }
      },
      set(value: string) {
        // Trim whitespace
        this.setDataValue('candidateName', value ? value.trim() : value);
      }
    },
    phone: {
      type: DataTypes.STRING(20),
      allowNull: true,
      validate: {
        isValidPhone(value: string) {
          if (value && value.trim() !== '' && !/^[+]?[1-9]\d{1,14}$/.test(value)) {
            throw new Error('Phone number format is invalid');
          }
        }
      },
      set(value: string | undefined) {
        // If empty string is provided, set to null
        this.setDataValue('phone', !value || value.trim() === '' ? undefined : value);
      }
    },
    email: {
      type: DataTypes.STRING(255),
      allowNull: true,
      validate: {
        isValidEmail(value: string) {
          if (value && value.trim() !== '' && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
            throw new Error('Email format is invalid');
          }
        }
      },
      set(value: string | undefined) {
        // If empty string is provided, set to null
        this.setDataValue('email', !value || value.trim() === '' ? undefined : value);
      }
    },
    filePath: {
      type: DataTypes.STRING(500),
      allowNull: true
    },
    fileName: {
      type: DataTypes.STRING(255),
      allowNull: true
    },
    fileSize: {
      type: DataTypes.INTEGER,
      allowNull: true,
      validate: {
        min: 0
      }
    },
    aiScore: {
      type: DataTypes.DECIMAL(3, 1),
      allowNull: true,
      validate: {
        min: 0,
        max: 100
      }
    },
    status: {
      type: DataTypes.ENUM('pending', 'reviewed', 'approved', 'rejected'),
      allowNull: false,
      defaultValue: 'pending'
    },
    uploadTime: {
      type: DataTypes.DATE,
      allowNull: false,
      defaultValue: DataTypes.NOW
    },
    reviewTime: {
      type: DataTypes.DATE,
      allowNull: true
    },
    reviewerId: {
      type: DataTypes.INTEGER,
      allowNull: true,
      references: {
        model: 'users',
        key: 'id'
      }
    },
    remarks: {
      type: DataTypes.TEXT,
      allowNull: true
    },
    createdAt: {
      type: DataTypes.DATE,
      allowNull: false,
      defaultValue: DataTypes.NOW,
    },
    updatedAt: {
      type: DataTypes.DATE,
      allowNull: false,
      defaultValue: DataTypes.NOW,
    },
  },
  {
    sequelize,
    modelName: 'Resume',
    tableName: 'resumes',
    timestamps: true,
    indexes: [
      {
        fields: ['userId']
      },
      {
        fields: ['status']
      },
      {
        fields: ['uploadTime']
      },
      {
        fields: ['candidateName']
      }
    ]
  }
);

// 定义关联关系
Resume.belongsTo(User, { foreignKey: 'userId', as: 'user' });
Resume.belongsTo(User, { foreignKey: 'reviewerId', as: 'reviewer' });
User.hasMany(Resume, { foreignKey: 'userId', as: 'resumes' });
User.hasMany(Resume, { foreignKey: 'reviewerId', as: 'reviewedResumes' });

export default Resume;