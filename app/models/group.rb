class Group < ActiveRecord::Base
  has_many :group_joins, :dependent => :destroy

  has_many :admins,
      :through => :group_joins,
      :class_name => "User",
      :source => :user,
      :conditions => ['group_joins.role = ?', 'admin']

  has_many :members,
      :through => :group_joins,
      :class_name => "User",
      :source => :user,
      :conditions => ['group_joins.role = ? AND group_joins.accepted = ?', 'member', true]

  has_many :candidates,
      :through => :group_joins,
      :class_name => "User",
      :source => :user,
      :conditions => ['group_joins.role = ? AND group_joins.accepted = ?', 'member', false]

  attr_accessible :name, :description

  validates :name
end
