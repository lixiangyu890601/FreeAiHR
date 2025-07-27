import { DataTypes, Model, Optional } from 'sequelize';
import { sequelize } from '../config/database';
import User from './User';

// 岗位属性接口
export interface PositionAttributes {
  id: number;
  userId: number;
  positionName: string;
  department: string;
  description?: string;
  requirements?: string;
  salaryMin?: number;
  salaryMax?: number;
  workLocation?: string;
  workType: 'full-time' | 'part-time' | 'contract' | 'internship';
  experienceLevel: 'entry' | 'junior' | 'mid' | 'senior' | 'lead';
  status: 'draft' | 'published' | 'paused' | 'closed';
  publishTime?: Date;
  closeTime?: Date;
  publisherId?: number;
  remarks?: string;
  createdAt: Date;
  updatedAt: Date;
}

// 创建时可选的属性
interface PositionCreationAttributes extends Optional<PositionAttributes, 'id' | 'publishTime' | 'closeTime' | 'publisherId' | 'remarks' | 'createdAt' | 'updatedAt'> {}

// 岗位模型类
class Position extends Model<PositionAttributes, PositionCreationAttributes> implements PositionAttributes {
  public id!: number;
  public userId!: number;
  public positionName!: string;
  public department!: string;
  public description?: string;
  public requirements?: string;
  public salaryMin?: number;
  public salaryMax?: number;
  public workLocation?: string;
  public workType!: 'full-time' | 'part-time' | 'contract' | 'internship';
  public experienceLevel!: 'entry' | 'junior' | 'mid' | 'senior' | 'lead';
  public status!: 'draft' | 'published' | 'paused' | 'closed';
  public publishTime?: Date;
  public closeTime?: Date;
  public publisherId?: number;
  public remarks?: string;
  public createdAt!: Date;
  public updatedAt!: Date;

  // 关联方法
  public getUser!: () => Promise<User>;
  public getPublisher!: () => Promise<User | null>;

  // 静态方法
  static async findByUserId(userId: number): Promise<Position[]> {
    return this.findAll({
      where: { userId },
      order: [['createdAt', 'DESC']]
    });
  }

  static async findByStatus(status: string): Promise<Position[]> {
    return this.findAll({
      where: { status },
      order: [['createdAt', 'DESC']]
    });
  }

  static async searchPositions(searchTerm: string): Promise<Position[]> {
    const { Op } = require('sequelize');
    return this.findAll({
      where: {
        [Op.or]: [
          { positionName: { [Op.like]: `%${searchTerm}%` } },
          { department: { [Op.like]: `%${searchTerm}%` } },
          { description: { [Op.like]: `%${searchTerm}%` } },
          { workLocation: { [Op.like]: `%${searchTerm}%` } }
        ]
      },
      order: [['createdAt', 'DESC']]
    });
  }
}

// 定义模型
Position.init(
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
    positionName: {
      type: DataTypes.STRING(255),
      allowNull: false,
      validate: {
        notEmpty: {
          msg: 'Position name is required'
        },
        len: {
          args: [1, 255],
          msg: 'Position name must be between 1 and 255 characters'
        }
      },
      set(value: string) {
        // Trim whitespace
        this.setDataValue('positionName', value ? value.trim() : value);
      }
    },
    department: {
      type: DataTypes.STRING(100),
      allowNull: false,
      validate: {
        notEmpty: {
          msg: 'Department is required'
        },
        len: {
          args: [1, 100],
          msg: 'Department must be between 1 and 100 characters'
        }
      },
      set(value: string) {
        // Trim whitespace
        this.setDataValue('department', value ? value.trim() : value);
      }
    },
    description: {
      type: DataTypes.TEXT,
      allowNull: true,
      set(value: string | undefined) {
        // If empty string is provided, set to null
        this.setDataValue('description', !value || value.trim() === '' ? undefined : value);
      }
    },
    requirements: {
      type: DataTypes.TEXT,
      allowNull: true,
      set(value: string | undefined) {
        // If empty string is provided, set to null
        this.setDataValue('requirements', !value || value.trim() === '' ? undefined : value);
      }
    },
    salaryMin: {
      type: DataTypes.INTEGER,
      allowNull: true,
      validate: {
        min: 0
      }
    },
    salaryMax: {
      type: DataTypes.INTEGER,
      allowNull: true,
      validate: {
        min: 0,
        isGreaterThanMin(value: number) {
          if (value && this.salaryMin && typeof this.salaryMin === 'number' && value < this.salaryMin) {
            throw new Error('Maximum salary must be greater than minimum salary');
          }
        }
      }
    },
    workLocation: {
      type: DataTypes.STRING(255),
      allowNull: true,
      set(value: string | undefined) {
        // If empty string is provided, set to null
        this.setDataValue('workLocation', !value || value.trim() === '' ? undefined : value);
      }
    },
    workType: {
      type: DataTypes.ENUM('full-time', 'part-time', 'contract', 'internship'),
      allowNull: false,
      defaultValue: 'full-time'
    },
    experienceLevel: {
      type: DataTypes.ENUM('entry', 'junior', 'mid', 'senior', 'lead'),
      allowNull: false,
      defaultValue: 'mid'
    },
    status: {
      type: DataTypes.ENUM('draft', 'published', 'paused', 'closed'),
      allowNull: false,
      defaultValue: 'draft'
    },
    publishTime: {
      type: DataTypes.DATE,
      allowNull: true
    },
    closeTime: {
      type: DataTypes.DATE,
      allowNull: true
    },
    publisherId: {
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
    modelName: 'Position',
    tableName: 'positions',
    timestamps: true,
    indexes: [
      {
        fields: ['userId']
      },
      {
        fields: ['status']
      },
      {
        fields: ['department']
      },
      {
        fields: ['workType']
      },
      {
        fields: ['experienceLevel']
      },
      {
        fields: ['createdAt']
      }
    ]
  }
);

export default Position;

// 定义关联关系 - 延迟导入以避免循环依赖
setTimeout(() => {
  const User = require('./User').default;
  Position.belongsTo(User, { foreignKey: 'userId', as: 'user' });
  Position.belongsTo(User, { foreignKey: 'publisherId', as: 'publisher' });
  User.hasMany(Position, { foreignKey: 'userId', as: 'positions' });
  User.hasMany(Position, { foreignKey: 'publisherId', as: 'publishedPositions' });
}, 0);