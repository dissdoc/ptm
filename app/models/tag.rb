class Tag < ActiveRecord::Base
  has_many :taggings, :dependent => :destroy
  has_many :photos, :through => :taggings

  def self.get(name)
    self.where("name = ?", name).last
  end
end
