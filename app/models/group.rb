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
      :conditions => ['group_joins.role = ? AND group_joins.accepted = ? AND group_joins.agree = ?', 'member', true, true]

  has_many :candidates,
      :through => :group_joins,
      :class_name => "User",
      :source => :user,
      :conditions => ['group_joins.role = ? AND (group_joins.accepted = ? OR group_joins.agree = ?)', 'member', false, false]

  has_many :photo_group_joins, :dependent => :destroy

  has_many :link_photos,
      :through => :photo_group_joins,
      :class_name => "Photo",
      :source => :photo

  has_many :dashboards, :as => :dashtable

  attr_accessible :name, :description

  validates :name, :presence => true
end
