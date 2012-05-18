class Tag < ActiveRecord::Base
  has_many :taggings, :dependent => :destroy

  has_many :photos,
      :through => :taggings,
      :class_name => 'Photo',
      :source => :photo

  has_many :users,
      :through => :taggings,
      :class_name => 'User',
      :source => :user

  def self.get(name)
    self.where("name = ?", name).last
  end
end
