import { DataTypes, Model, Optional } from 'sequelize';
import bcrypt from 'bcryptjs';
import { sequelize } from '../config/database';

// 用户属性接口
export interface UserAttributes {
  id: number;
  username: string;
  email: string;
  phone?: string;
  password: string;
  role: 'user' | 'admin';
  isActive: boolean;
  lastLogin?: Date;
  createdAt?: Date;
  updatedAt?: Date;
}

// 创建时可选的属性
interface UserCreationAttributes extends Optional<UserAttributes, 'id' | 'isActive' | 'lastLogin'> {}

// 用户模型类
class User extends Model<UserAttributes, UserCreationAttributes> implements UserAttributes {
  public id!: number;
  public username!: string;
  public email!: string;
  public phone?: string;
  public password!: string;
  public role!: 'user' | 'admin';
  public isActive!: boolean;
  public lastLogin?: Date;
  public readonly createdAt!: Date;
  public readonly updatedAt!: Date;

  // 实例方法：密码验证
  public async comparePassword(candidatePassword: string): Promise<boolean> {
    return bcrypt.compare(candidatePassword, this.password);
  }

  // 类方法：查找用户（排除密码）
  public static async findByIdSafe(id: number): Promise<User | null> {
    return this.findByPk(id, {
      attributes: { exclude: ['password'] }
    });
  }

  // 类方法：通过邮箱查找用户
  public static async findByEmail(email: string): Promise<User | null> {
    return this.findOne({ where: { email: email.toLowerCase() } });
  }

  // 类方法：通过手机号查找用户
  public static async findByPhone(phone: string): Promise<User | null> {
    return this.findOne({ where: { phone } });
  }

  // 类方法：通过邮箱或手机号查找用户
  public static async findByEmailOrPhone(identifier: string): Promise<User | null> {
    const { Op } = require('sequelize');
    return this.findOne({
      where: {
        [Op.or]: [
          { email: identifier.toLowerCase() },
          { phone: identifier }
        ]
      }
    });
  }
}

User.init({
  id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true
  },
  username: {
    type: DataTypes.STRING(30),
    allowNull: false,
    unique: true,
    validate: {
      len: [3, 30],
      notEmpty: true
    }
  },
  email: {
    type: DataTypes.STRING,
    allowNull: false,
    unique: true,
    validate: {
      isEmail: true,
      notEmpty: true
    },
    set(value: string) {
      this.setDataValue('email', value.toLowerCase());
    }
  },
  phone: {
    type: DataTypes.STRING(20),
    allowNull: true,
    unique: true,
    validate: {
      is: /^[+]?[1-9]\d{1,14}$/,  // 国际电话号码格式
      notEmpty: true
    }
  },
  password: {
    type: DataTypes.STRING,
    allowNull: false,
    validate: {
      len: [6, 255],
      notEmpty: true
    }
  },
  role: {
    type: DataTypes.ENUM('user', 'admin'),
    defaultValue: 'user',
    allowNull: false
  },
  isActive: {
    type: DataTypes.BOOLEAN,
    defaultValue: true,
    allowNull: false
  },
  lastLogin: {
    type: DataTypes.DATE,
    allowNull: true
  }
}, {
  tableName: 'users',
  timestamps: true,
  sequelize,
  hooks: {
    // 密码加密钩子
    beforeSave: async (user: User) => {
      if (user.changed('password')) {
        const salt = await bcrypt.genSalt(12);
        user.password = await bcrypt.hash(user.password, salt);
      }
    }
  }
});

export default User;