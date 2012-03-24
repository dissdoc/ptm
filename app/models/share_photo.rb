class SharePhoto < ActiveRecord::Base
  belongs_to :photo

  attr_accessible :share

  def sharing!
    update_attribute(:share, true)
  end

  def reject!
    update_attribute(:share, false)
  end
end
